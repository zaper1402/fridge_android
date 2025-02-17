package com.ashir.fridge.http
import androidx.annotation.StringDef
import com.ashir.fridge.utils.managers.AppDeviceManager
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.threemusketeers.dliverCustomer.main.utils.extensions.forEachSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.Serializable


object HttpDomainManager {
    // http constants
    const val HTTP = "http://"
    const val HTTPS = "https://"
    const val DOMAIN_OVERRIDE_PRODUCTION = "DOMAIN_OVERRIDE_PRODUCTION"
    const val DOMAIN_OVERRIDE_STAGING = "DOMAIN_OVERRIDE_STAGING"
    const val DOMAIN_OVERRIDE_DOCKER = "DOMAIN_OVERRIDE_DOCKER"
    const val DOMAIN_OVERRIDE_LOCALHOST = "DOMAIN_OVERRIDE_LOCALHOST"

    private var PRODUCTION_DOMAIN = "api.dliver.com"
    private var STAGING_DOMAIN = "ec2-54-227-2-190.compute-1.amazonaws.com:8000"
    private var LOCALHOST_DOMAIN = "localhost:8000"
    private var EMULATOR_DOMAIN = "10.0.2.2:8000"
    private var DOCKER_API = SharedPrefUtil.getDefaultInstance().getString(SharedPrefConstants.DOCKER_IP_ADDRESS, defaultValue = "")
    private var prodDomainMap  : Map<String,String>  = SharedPrefUtil.getDefaultInstance().getDataObject(
        SharedPrefConstants.PRODUCTION_DOMAIN_MAP,Map::class.java) as? Map<String, String> ?:  mapOf()
    private var stagingDomainMap : Map<String,String> = SharedPrefUtil.getDefaultInstance().getDataObject(
        SharedPrefConstants.STAGING_DOMAIN_MAP,Map::class.java) as? Map<String, String> ?:  mapOf()
    private var localHostDomainMap : Map<String,String> = SharedPrefUtil.getDefaultInstance().getDataObject(
        SharedPrefConstants.LOCALHOST_DOMAIN_MAP,Map::class.java) as? Map<String, String> ?:  mapOf()

    private var BASE_DOMAIN_INFO = getBaseDomainInfo()
    private var mDomainMap = mutableMapOf<String, String>()
    private var allDomainInfoList: MutableList<DomainInfo>
    private val isEmulator by lazy { AppDeviceManager.isEmulator()}
    var isDocker = false
        private set
    var isProduction = true
        private set

    var isLocalhost = false
        private set

    init {
        allDomainInfoList = mutableListOf(BASE_DOMAIN_INFO)
        isProduction = SharedPrefUtil.getDefaultInstance().getBoolean(SharedPrefConstants.IS_PRODUCTION_SERVER, true)
        isDocker = SharedPrefUtil.getDefaultInstance().getBoolean(SharedPrefConstants.IS_DOCKER_SERVER, false)
        isLocalhost = SharedPrefUtil.getDefaultInstance().getBoolean(SharedPrefConstants.IS_LOCALHOST_SERVER, false)
        toggleProductionOrStagingConfiguration(isProduction, isDocker, isLocalhost,true)
    }

    fun reInit(){
        DOCKER_API = SharedPrefUtil.getDefaultInstance().getString(SharedPrefConstants.DOCKER_IP_ADDRESS, defaultValue = "")
        BASE_DOMAIN_INFO = getBaseDomainInfo()
        allDomainInfoList = mutableListOf(BASE_DOMAIN_INFO)
        toggleProductionOrStagingConfiguration(isProduction, isDocker, isLocalhost)
    }

    fun updateDomainMaps(stagingDomainMap: Map<String, String>?, prodDomainMap: Map<String, String>?){
        SharedPrefUtil.getDefaultInstance().saveDataObject(SharedPrefConstants.PRODUCTION_DOMAIN_MAP, prodDomainMap)
        SharedPrefUtil.getDefaultInstance().saveDataObject(SharedPrefConstants.STAGING_DOMAIN_MAP,stagingDomainMap)
        HttpDomainManager.stagingDomainMap = stagingDomainMap?: mapOf()
        HttpDomainManager.prodDomainMap = prodDomainMap?: mapOf()
    }

    fun initializeOnStartup() {
        // do any other initialization here. As main initialization of toggleProdStag happened in init block {}
    }

    fun toggleProductionOrStagingConfiguration(isProduction: Boolean, isDocker:Boolean, isLocalhost: Boolean, isInitCall : Boolean = false) {
        MainScope().launch(Dispatchers.IO) {
            if(isInitCall.not() || SharedPrefUtil.getDefaultInstance().contains(SharedPrefConstants.IS_PRODUCTION_SERVER).not()){
                SharedPrefUtil.getDefaultInstance().saveBoolean(SharedPrefConstants.IS_PRODUCTION_SERVER, isProduction)
            }
            if(isInitCall.not() || SharedPrefUtil.getDefaultInstance().contains(SharedPrefConstants.IS_DOCKER_SERVER).not()){
                SharedPrefUtil.getDefaultInstance().saveBoolean(SharedPrefConstants.IS_DOCKER_SERVER, isDocker)
            }
        }
        HttpDomainManager.isProduction = isProduction // to make the correct value even in current session.
        HttpDomainManager.isDocker = isDocker // to make the current value for docker in the current session.
        HttpDomainManager.isLocalhost = isLocalhost
        for (domainInfo in allDomainInfoList) {
            mDomainMap[domainInfo.domainType] = domainInfo.getDomainBasedOnConfig(isProduction,isDocker,isLocalhost, isEmulator,domainInfo.domainType)
        }
        doOverrideDomainsPresentInSharedPrefs(isProduction,isDocker,isLocalhost)
    }

    private fun doOverrideDomainsPresentInSharedPrefs(isProduction: Boolean, isDocker: Boolean, isLocalhost: Boolean) {
        val domainOverrideConfigurationParent: DomainOverrideConfigurationParent? = if(isLocalhost){
            SharedPrefUtil.getDefaultInstance().getDataObject(DOMAIN_OVERRIDE_LOCALHOST, DomainOverrideConfigurationParent::class.java)
        } else if (isProduction) {
            SharedPrefUtil.getDefaultInstance().getDataObject(DOMAIN_OVERRIDE_PRODUCTION, DomainOverrideConfigurationParent::class.java)
        } else {
            if (isDocker) {
                SharedPrefUtil.getDefaultInstance().getDataObject(DOMAIN_OVERRIDE_DOCKER, DomainOverrideConfigurationParent::class.java)
            } else {
                SharedPrefUtil.getDefaultInstance().getDataObject(DOMAIN_OVERRIDE_STAGING, DomainOverrideConfigurationParent::class.java)
            }
        }
        domainOverrideConfigurationParent?.mDomainSet?.forEachSafe {
            mDomainMap[it.domainType] = it.domainUrl
        }
    }

    /*
    *  AC packet flow to change domain url of any particular domainType for production. Please refer to [DomainType] class for valid domain types.
    * */
    fun overrideDomainConfigProduction(@DomainType domainType: String, productionUrlWithHttps: String) {
        if (productionUrlWithHttps.isEmpty() || productionUrlWithHttps.startsWith(HTTP).not()) {
            // invalid url not overriding.
            return
        }
        allDomainInfoList.find { it.domainType == domainType }?.productionDomain = productionUrlWithHttps

        val domainOverrideConfigurationParent = SharedPrefUtil.getDefaultInstance().getDataObject(
            DOMAIN_OVERRIDE_PRODUCTION, DomainOverrideConfigurationParent::class.java) ?: DomainOverrideConfigurationParent()

        domainOverrideConfigurationParent.mDomainSet.add(DomainOverrideConfiguration(domainType, productionUrlWithHttps))
        SharedPrefUtil.getDefaultInstance().saveDataObject(DOMAIN_OVERRIDE_PRODUCTION, domainOverrideConfigurationParent)
    }

    /*
    *  AC packet flow to change domain url of any particular domainType for staging. Please refer to [DomainType] class for valid domain types.
    * */
    fun overrideDomainConfigStaging(@DomainType domainType: String, stagingUrlWithHttps: String) {
        if (stagingUrlWithHttps.isEmpty() || stagingUrlWithHttps.startsWith(HTTP).not()) {
            return
        }
        allDomainInfoList.find { it.domainType == domainType }?.run {
            stagingDomain = stagingUrlWithHttps
            isStagingHttpsDomain = stagingUrlWithHttps.startsWith(HTTPS)
        }

        val domainOverrideConfigurationParent = SharedPrefUtil.getDefaultInstance().getDataObject(
            DOMAIN_OVERRIDE_STAGING, DomainOverrideConfigurationParent::class.java)
                ?: DomainOverrideConfigurationParent()

        domainOverrideConfigurationParent.mDomainSet.add(DomainOverrideConfiguration(domainType, stagingUrlWithHttps))
        SharedPrefUtil.getDefaultInstance().saveDataObject(DOMAIN_OVERRIDE_STAGING, domainOverrideConfigurationParent)
    }

    fun generateUrl(@DomainType domainType: String = DomainType.BASE_DOMAIN, endPoint: String): String {  // default domainType is made as Base_domain
        val endPointValue = if (endPoint.startsWith("/")) endPoint else "/$endPoint"
        return mDomainMap[domainType] + endPointValue
    }

    fun resetProductionFlag() {
        isProduction = true  // reset to default state true
        isDocker = false
    }
    private fun getBaseDomainInfo(): DomainInfo {
        return DomainInfo(
            DomainType.BASE_DOMAIN, prodDomainMap.getOrDefault(DomainType.BASE_DOMAIN, PRODUCTION_DOMAIN),
            stagingDomainMap.getOrDefault(DomainType.BASE_DOMAIN, STAGING_DOMAIN),
            localHostDomainMap.getOrDefault(DomainType.BASE_DOMAIN, LOCALHOST_DOMAIN),
            EMULATOR_DOMAIN, "$DOCKER_API:3000")
    }
}

// Add any domains u want in future.
@StringDef(
    DomainType.BASE_DOMAIN,
    DomainType.DOCKER_EXCEPTION_DOMAIN
)
annotation class DomainType {
    companion object {
        const val BASE_DOMAIN = "BASE_DOMAIN"
        const val DOCKER_EXCEPTION_DOMAIN = "DOCKER_EXCEPTION_DOMAIN"
    }
}

/*
*  productionUrl n stagingUrl should not start with https or http, because that will be appended explicitly.See [DomainInfo.getUrlBasedOnConfig()]
*  Append Logic :
*    if production then "https://" append to productionUrl
*    if staging then  if isStagingHttpsDomain == true  then  "https://" append to stagingUrl
*                     else  "http://" append to stagingUrl
* */
class DomainInfo(@DomainType val domainType: String, var productionDomain: String, var stagingDomain: String, val localHostDomain: String, val emulatorDomain: String, var dockerUrl : String, var isStagingHttpsDomain: Boolean = true) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (domainType == (other as? DomainInfo)?.domainType) return true
        return false
    }

    override fun hashCode(): Int {
        return domainType.hashCode()
    }

    fun getDomainBasedOnConfig(isProduction: Boolean, isDocker: Boolean, isLocalhost: Boolean, isEmulator: Boolean, domainType: String? = null): String {
        return if(isLocalhost && isEmulator){
            HttpDomainManager.HTTP + emulatorDomain
        }else if(isLocalhost){
            HttpDomainManager.HTTP + localHostDomain
        }else if (isProduction) {
            HttpDomainManager.HTTPS + productionDomain
        } else {
            if (isDocker) {
                HttpDomainManager.HTTP + dockerUrl
            } else {
//                if (isStagingHttpsDomain) HttpDomainManager.HTTPS + stagingDomain else HttpDomainManager.HTTP + stagingDomain
                HttpDomainManager.HTTPS + stagingDomain
            }
        }
    }


}

class DomainOverrideConfigurationParent() : Serializable {
    val mDomainSet: MutableSet<DomainOverrideConfiguration> = mutableSetOf()
}

class DomainOverrideConfiguration(val domainType: String, val domainUrl: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DomainOverrideConfiguration

        if (domainType != other.domainType) return false

        return true
    }

    override fun hashCode(): Int {
        return domainType.hashCode()
    }
}
