package com.threemusketeers.dliverCustomer.main.onboarding.enums

enum class CountryCode(val code : String,val phoneCode : String) {
    INDIA("IN","+91"),
    USA("US","+1"),
    FINLAND("FI","+358")
}

enum class StatesCodes(val state: String, val stateCode: String) {
    AndhraPradesh("Andhra Pradesh", "AP"),
    ArunachalPradesh("Arunachal Pradesh", "AR"),
    Assam("Assam", "AS"),
    Bihar("Bihar", "BR"),
    Chhattisgarh("Chhattisgarh", "CG"),
    Goa("Goa", "GA"),
    Gujarat("Gujarat", "GJ"),
    Haryana("Haryana", "HR"),
    HimachalPradesh("Himachal Pradesh", "HP"),
    Jharkhand("Jharkhand", "JH"),
    Karnataka("Karnataka", "KA"),
    Kerala("Kerala", "KL"),
    MadhyaPradesh("Madhya Pradesh", "MP"),
    Maharashtra("Maharashtra", "MH"),
    Manipur("Manipur", "MN"),
    Meghalaya("Meghalaya", "ML"),
    Mizoram("Mizoram", "MZ"),
    Nagaland("Nagaland", "NL"),
    Odisha("Odisha", "OR"),
    Punjab("Punjab", "PB"),
    Rajasthan("Rajasthan", "RJ"),
    Sikkim("Sikkim", "SK"),
    TamilNadu("Tamil Nadu", "TN"),
    Telangana("Telangana", "TS"),
    Tripura("Tripura", "TR"),
    UttarPradesh("Uttar Pradesh", "UP"),
    Uttarakhand("Uttarakhand", "UK"),
    WestBengal("West Bengal", "WB"),
    Delhi("Delhi", "DL"),
    JammuKashmir("Jammu & Kashmir", "JK"),
    Ladakh("Ladakh", "LA"),
    Puducherry("Puducherry", "PY"),
    Chandigarh("Chandigarh", "CH"),
    AndamanNicobar("Andaman & Nicobar Islands", "AN"),
    DadraNagarHaveli("Dadra & Nagar Haveli", "DN"),
    DamanDiu("Daman & Diu", "DD"),
    Lakshadweep("Lakshadweep", "LD");

    companion object {
        fun getFromNameOrNull(state:String): StatesCodes? {
            return try {
                valueOf(state)
            }catch (e: IllegalArgumentException){
                null
            }
        }
    }
}

enum class CityCodes(val city: String, val stateCode: StatesCodes) {
    Agartala("Agartala", StatesCodes.Tripura),
    Ahmedabad("Ahmedabad", StatesCodes.Gujarat),
    Aizawl("Aizawl", StatesCodes.Mizoram),
    Amritsar("Amritsar", StatesCodes.Punjab),
    Bangalore("Bangalore", StatesCodes.Karnataka),
    Bhopal("Bhopal", StatesCodes.MadhyaPradesh),
    Bhubaneswar("Bhubaneswar", StatesCodes.Odisha),
    Chandigarh("Chandigarh", StatesCodes.Chandigarh),
    Chennai("Chennai", StatesCodes.TamilNadu),
    Coimbatore("Coimbatore", StatesCodes.TamilNadu),
    Cuttack("Cuttack", StatesCodes.Odisha),
    Daman("Daman", StatesCodes.DamanDiu),
    Dehradun("Dehradun", StatesCodes.Uttarakhand),
    Dibrugarh("Dibrugarh", StatesCodes.Assam),
    Faridabad("Faridabad", StatesCodes.Haryana),
    Gangtok("Gangtok", StatesCodes.Sikkim),
    Gaya("Gaya", StatesCodes.Bihar),
    Gurgaon("Gurgaon", StatesCodes.Haryana),
    Guwahati("Guwahati", StatesCodes.Assam),
    Howrah("Howrah", StatesCodes.WestBengal),
    Hyderabad("Hyderabad", StatesCodes.Telangana),
    Imphal("Imphal", StatesCodes.Manipur),
    Indore("Indore", StatesCodes.MadhyaPradesh),
    Itanagar("Itanagar", StatesCodes.ArunachalPradesh),
    Jaipur("Jaipur", StatesCodes.Rajasthan),
    Jammu("Jammu", StatesCodes.JammuKashmir),
    Jamshedpur("Jamshedpur", StatesCodes.Jharkhand),
    Kanpur("Kanpur", StatesCodes.UttarPradesh),
    Kochi("Kochi", StatesCodes.Kerala),
    Kohima("Kohima", StatesCodes.Nagaland),
    Kolkata("Kolkata", StatesCodes.WestBengal),
    Leh("Leh", StatesCodes.Ladakh),
    Ludhiana("Ludhiana", StatesCodes.Punjab),
    Lucknow("Lucknow", StatesCodes.UttarPradesh),
    Mumbai("Mumbai", StatesCodes.Maharashtra),
    Mysore("Mysore", StatesCodes.Karnataka),
    NewDelhi("New Delhi", StatesCodes.Delhi),
    Panaji("Panaji", StatesCodes.Goa),
    Patna("Patna", StatesCodes.Bihar),
    PortBlair("Port Blair", StatesCodes.AndamanNicobar),
    Puducherry("Puducherry", StatesCodes.Puducherry),
    Pune("Pune", StatesCodes.Maharashtra),
    Raipur("Raipur", StatesCodes.Chhattisgarh),
    Ranchi("Ranchi", StatesCodes.Jharkhand),
    Shillong("Shillong", StatesCodes.Meghalaya),
    Shimla("Shimla", StatesCodes.HimachalPradesh),
    Silvassa("Silvassa", StatesCodes.DadraNagarHaveli),
    Srinagar("Srinagar", StatesCodes.JammuKashmir),
    Surat("Surat", StatesCodes.Gujarat),
    Thiruvananthapuram("Thiruvananthapuram", StatesCodes.Kerala),
    Udaipur("Udaipur", StatesCodes.Rajasthan),
    Visakhapatnam("Visakhapatnam", StatesCodes.AndhraPradesh),
    Vijayawada("Vijayawada", StatesCodes.AndhraPradesh);

    companion object{
        fun getFromNameOrNull(city:String): CityCodes? {
            return try {
                valueOf(city)
            }catch (e: IllegalArgumentException){
                null
            }
        }
    }
}

