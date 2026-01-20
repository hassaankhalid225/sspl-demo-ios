package com.sspl.ui.components.countrypicker


data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String,
    val country: CountryName
)

val Pakistan = Country(
    name = "Pakistan", code = "PK", dialCode = "+92", flag = "🇵🇰", country = CountryName.PAKISTAN
)

val allCountries by lazy {
    listOf(
        Country("Afghanistan", "AF", "+93", "🇦🇫", CountryName.AFGHANISTAN),
        Country("Albania", "AL", "+355", "🇦🇱", CountryName.ALBANIA),
        Country("Algeria", "DZ", "+213", "🇩🇿", CountryName.ALGERIA),
        Country("Andorra", "AD", "+376", "🇦🇩", CountryName.ANDORRA),
        Country("Angola", "AO", "+244", "🇦🇴", CountryName.ANGOLA),
        Country("Antigua and Barbuda", "AG", "+1-268", "🇦🇬", CountryName.ANTIGUA_AND_BARBUDA),
        Country("Argentina", "AR", "+54", "🇦🇷", CountryName.ARGENTINA),
        Country("Armenia", "AM", "+374", "🇦🇲", CountryName.ARMENIA),
        Country("Australia", "AU", "+61", "🇦🇺", CountryName.AUSTRALIA),
        Country("Austria", "AT", "+43", "🇦🇹", CountryName.AUSTRIA),
        Country("Azerbaijan", "AZ", "+994", "🇦🇿", CountryName.AZERBAIJAN),
        Country("Bahamas", "BS", "+1-242", "🇧🇸", CountryName.BAHAMAS),
        Country("Bahrain", "BH", "+973", "🇧🇭", CountryName.BAHRAIN),
        Country("Bangladesh", "BD", "+880", "🇧🇩", CountryName.BANGLADESH),
        Country("Barbados", "BB", "+1-246", "🇧🇧", CountryName.BARBADOS),
        Country("Belarus", "BY", "+375", "🇧🇾", CountryName.BELARUS),
        Country("Belgium", "BE", "+32", "🇧🇪", CountryName.BELGIUM),
        Country("Belize", "BZ", "+501", "🇧🇿", CountryName.BELIZE),
        Country("Benin", "BJ", "+229", "🇧🇯", CountryName.BENIN),
        Country("Bhutan", "BT", "+975", "🇧🇹", CountryName.BHUTAN),
        Country("Bolivia", "BO", "+591", "🇧🇴", CountryName.BOLIVIA),
        Country("Bosnia and Herzegovina", "BA", "+387", "🇧🇦", CountryName.BOSNIA_AND_HERZEGOVINA),
        Country("Botswana", "BW", "+267", "🇧🇼", CountryName.BOTSWANA),
        Country("Brazil", "BR", "+55", "🇧🇷", CountryName.BRAZIL),
        Country("Brunei", "BN", "+673", "🇧🇳", CountryName.BRUNEI),
        Country("Bulgaria", "BG", "+359", "🇧🇬", CountryName.BULGARIA),
        Country("Burkina Faso", "BF", "+226", "🇧🇫", CountryName.BURKINA_FASO),
        Country("Burundi", "BI", "+257", "🇧🇮", CountryName.BURUNDI),
        Country("Cambodia", "KH", "+855", "🇰🇭", CountryName.CAMBODIA),
        Country("Cameroon", "CM", "+237", "🇨🇲", CountryName.CAMEROON),
        Country("Canada", "CA", "+1", "🇨🇦", CountryName.CANADA),
        Country("Cape Verde", "CV", "+238", "🇨🇻", CountryName.CAPE_VERDE),
        Country(
            "Central African Republic", "CF", "+236", "🇨🇫", CountryName.CENTRAL_AFRICAN_REPUBLIC
        ),
        Country("Chad", "TD", "+235", "🇹🇩", CountryName.CHAD),
        Country("Chile", "CL", "+56", "🇨🇱", CountryName.CHILE),
        Country("China", "CN", "+86", "🇨🇳", CountryName.CHINA),
        Country("Colombia", "CO", "+57", "🇨🇴", CountryName.COLOMBIA),
        Country("Comoros", "KM", "+269", "🇰🇲", CountryName.COMOROS),
        Country("Congo", "CG", "+242", "🇨🇬", CountryName.CONGO),
        Country("Costa Rica", "CR", "+506", "🇨🇷", CountryName.COSTA_RICA),
        Country("Croatia", "HR", "+385", "🇭🇷", CountryName.CROATIA),
        Country("Cuba", "CU", "+53", "🇨🇺", CountryName.CUBA),
        Country("Cyprus", "CY", "+357", "🇨🇾", CountryName.CYPRUS),
        Country("Czech Republic", "CZ", "+420", "🇨🇿", CountryName.CZECH_REPUBLIC),
        Country(
            "Democratic Republic of the Congo",
            "CD",
            "+243",
            "🇨🇩",
            CountryName.DEMOCRATIC_REPUBLIC_OF_THE_CONGO
        ),
        Country("Denmark", "DK", "+45", "🇩🇰", CountryName.DENMARK),
        Country("Djibouti", "DJ", "+253", "🇩🇯", CountryName.DJIBOUTI),
        Country("Dominica", "DM", "+1-767", "🇩🇲", CountryName.DOMINICA),
        Country(
            "Dominican Republic",
            "DO",
            "+1-809, +1-829, +1-849",
            "🇩🇴",
            CountryName.DOMINICAN_REPUBLIC
        ),
        Country("East Timor", "TL", "+670", "🇹🇱", CountryName.EAST_TIMOR),
        Country("Ecuador", "EC", "+593", "🇪🇨", CountryName.ECUADOR),
        Country("Egypt", "EG", "+20", "🇪🇬", CountryName.EGYPT),
        Country("El Salvador", "SV", "+503", "🇸🇻", CountryName.EL_SALVADOR),
        Country("Equatorial Guinea", "GQ", "+240", "🇬🇶", CountryName.EQUATORIAL_GUINEA),
        Country("Eritrea", "ER", "+291", "🇪🇷", CountryName.ERITREA),
        Country("Estonia", "EE", "+372", "🇪🇪", CountryName.ESTONIA),
        Country("Ethiopia", "ET", "+251", "🇪🇹", CountryName.ETHIOPIA),
        Country("Fiji", "FJ", "+679", "🇫🇯", CountryName.FIJI),
        Country("Finland", "FI", "+358", "🇫🇮", CountryName.FINLAND),
        Country("France", "FR", "+33", "🇫🇷", CountryName.FRANCE),
        Country("Gabon", "GA", "+241", "🇬🇦", CountryName.GABON),
        Country("Gambia", "GM", "+220", "🇬🇲", CountryName.GAMBIA),
        Country("Georgia", "GE", "+995", "🇬🇪", CountryName.GEORGIA),
        Country("Germany", "DE", "+49", "🇩🇪", CountryName.GERMANY),
        Country("Ghana", "GH", "+233", "🇬🇭", CountryName.GHANA),
        Country("Greece", "GR", "+30", "🇬🇷", CountryName.GREECE),
        Country("Grenada", "GD", "+1-473", "🇬🇩", CountryName.GRENADA),
        Country("Guatemala", "GT", "+502", "🇬🇹", CountryName.GUATEMALA),
        Country("Guinea", "GN", "+224", "🇬🇳", CountryName.GUINEA),
        Country("Guinea-Bissau", "GW", "+245", "🇬🇼", CountryName.GUINEA_BISSAU),
        Country("Guyana", "GY", "+592", "🇬🇾", CountryName.GUYANA),
        Country("Haiti", "HT", "+509", "🇭🇹", CountryName.HAITI),
        Country("Honduras", "HN", "+504", "🇭🇳", CountryName.HONDURAS),
        Country("Hungary", "HU", "+36", "🇭🇺", CountryName.HUNGARY),
        Country("Iceland", "IS", "+354", "🇮🇸", CountryName.ICELAND),
        Country("India", "IN", "+91", "🇮🇳", CountryName.INDIA),
        Country("Indonesia", "ID", "+62", "🇮🇩", CountryName.INDONESIA),
        Country("Iran", "IR", "+98", "🇮🇷", CountryName.IRAN),
        Country("Iraq", "IQ", "+964", "🇮🇶", CountryName.IRAQ),
        Country("Ireland", "IE", "+353", "🇮🇪", CountryName.IRELAND),
        Country("Israel", "IL", "+972", "🇮🇱", CountryName.ISRAEL),
        Country("Italy", "IT", "+39", "🇮🇹", CountryName.ITALY),
        Country("Jamaica", "JM", "+1-876", "🇯🇲", CountryName.JAMAICA),
        Country("Japan", "JP", "+81", "🇯🇵", CountryName.JAPAN),
        Country("Jordan", "JO", "+962", "🇯🇴", CountryName.JORDAN),
        Country("Kazakhstan", "KZ", "+7", "🇰🇿", CountryName.KAZAKHSTAN),
        Country("Kenya", "KE", "+254", "🇰🇪", CountryName.KENYA),
        Country("Kiribati", "KI", "+686", "🇰🇮", CountryName.KIRIBATI),
        Country("Kuwait", "KW", "+965", "🇰🇼", CountryName.KUWAIT),
        Country("Kyrgyzstan", "KG", "+996", "🇰🇬", CountryName.KYRGYZSTAN),
        Country("Laos", "LA", "+856", "🇱🇦", CountryName.LAOS),
        Country("Latvia", "LV", "+371", "🇱🇻", CountryName.LATVIA),
        Country("Lebanon", "LB", "+961", "🇱🇧", CountryName.LEBANON),
        Country("Lesotho", "LS", "+266", "🇱🇸", CountryName.LESOTHO),
        Country("Liberia", "LR", "+231", "🇱🇷", CountryName.LIBERIA),
        Country("Libya", "LY", "+218", "🇱🇾", CountryName.LIBYA),
        Country("Liechtenstein", "LI", "+423", "🇱🇮", CountryName.LIECHTENSTEIN),
        Country("Lithuania", "LT", "+370", "🇱🇹", CountryName.LITHUANIA),
        Country("Luxembourg", "LU", "+352", "🇱🇺", CountryName.LUXEMBOURG),
        Country("Madagascar", "MG", "+261", "🇲🇬", CountryName.MADAGASCAR),
        Country("Malawi", "MW", "+265", "🇲🇼", CountryName.MALAWI),
        Country("Malaysia", "MY", "+60", "🇲🇾", CountryName.MALAYSIA),
        Country("Maldives", "MV", "+960", "🇲🇻", CountryName.MALDIVES),
        Country("Mali", "ML", "+223", "🇲🇱", CountryName.MALI),
        Country("Malta", "MT", "+356", "🇲🇹", CountryName.MALTA),
        Country("Marshall Islands", "MH", "+692", "🇲🇭", CountryName.MARSHALL_ISLANDS),
        Country("Mauritania", "MR", "+222", "🇲🇷", CountryName.MAURITANIA),
        Country("Mauritius", "MU", "+230", "🇲🇺", CountryName.MAURITIUS),
        Country("Mexico", "MX", "+52", "🇲🇽", CountryName.MEXICO),
        Country("Micronesia", "FM", "+691", "🇫🇲", CountryName.MICRONESIA),
        Country("Moldova", "MD", "+373", "🇲🇩", CountryName.MOLDOVA),
        Country("Monaco", "MC", "+377", "🇲🇨", CountryName.MONACO),
        Country("Mongolia", "MN", "+976", "🇲🇳", CountryName.MONGOLIA),
        Country("Montenegro", "ME", "+382", "🇲🇪", CountryName.MONTENEGRO),
        Country("Morocco", "MA", "+212", "🇲🇦", CountryName.MOROCCO),
        Country("Mozambique", "MZ", "+258", "🇲🇿", CountryName.MOZAMBIQUE),
        Country("Myanmar", "MM", "+95", "🇲🇲", CountryName.MYANMAR),
        Country("Namibia", "NA", "+264", "🇳🇦", CountryName.NAMIBIA),
        Country("Nauru", "NR", "+674", "🇳🇷", CountryName.NAURU),
        Country("Nepal", "NP", "+977", "🇳🇵", CountryName.NEPAL),
        Country("Netherlands", "NL", "+31", "🇳🇱", CountryName.NETHERLANDS),
        Country("New Zealand", "NZ", "+64", "🇳🇿", CountryName.NEW_ZEALAND),
        Country("Nicaragua", "NI", "+505", "🇳🇮", CountryName.NICARAGUA),
        Country("Niger", "NE", "+227", "🇳🇪", CountryName.NIGER),
        Country("Nigeria", "NG", "+234", "🇳🇬", CountryName.NIGERIA),
        Country("North Korea", "KP", "+850", "🇰🇵", CountryName.NORTH_KOREA),
        Country("North Macedonia", "MK", "+389", "🇲🇰", CountryName.NORTH_MACEDONIA),
        Country("Norway", "NO", "+47", "🇳🇴", CountryName.NORWAY),
        Country("Oman", "OM", "+968", "🇴🇲", CountryName.OMAN),
        Country("Pakistan", "PK", "+92", "🇵🇰", CountryName.PAKISTAN),
        Country("Palau", "PW", "+680", "🇵🇼", CountryName.PALAU),
        Country("Palestine", "PS", "+970", "🇵🇸", CountryName.PALESTINE),
        Country("Panama", "PA", "+507", "🇵🇦", CountryName.PANAMA),
        Country("Papua New Guinea", "PG", "+675", "🇵🇬", CountryName.PAPUA_NEW_GUINEA),
        Country("Paraguay", "PY", "+595", "🇵🇾", CountryName.PARAGUAY),
        Country("Peru", "PE", "+51", "🇵🇪", CountryName.PERU),
        Country("Philippines", "PH", "+63", "🇵🇭", CountryName.PHILIPPINES),
        Country("Poland", "PL", "+48", "🇵🇱", CountryName.POLAND),
        Country("Portugal", "PT", "+351", "🇵🇹", CountryName.PORTUGAL),
        Country("Qatar", "QA", "+974", "🇶🇦", CountryName.QATAR),
        Country("Romania", "RO", "+40", "🇷🇴", CountryName.ROMANIA),
        Country("Russia", "RU", "+7", "🇷🇺", CountryName.RUSSIA),
        Country("Rwanda", "RW", "+250", "🇷🇼", CountryName.RWANDA),
        Country("Saint Kitts and Nevis", "KN", "+1-869", "🇰🇳", CountryName.SAINT_KITTS_AND_NEVIS),
        Country("Saint Lucia", "LC", "+1-758", "🇱🇨", CountryName.SAINT_LUCIA),
        Country(
            "Saint Vincent and the Grenadines",
            "VC",
            "+1-784",
            "🇻🇨",
            CountryName.SAINT_VINCENT_AND_THE_GRENADINES
        ),
        Country("Samoa", "WS", "+685", "🇼🇸", CountryName.SAMOA),
        Country("San Marino", "SM", "+378", "🇸🇲", CountryName.SAN_MARINO),
        Country("Sao Tome and Principe", "ST", "+239", "🇸🇹", CountryName.SAO_TOME_AND_PRINCIPE),
        Country("Saudi Arabia", "SA", "+966", "🇸🇦", CountryName.SAUDI_ARABIA),
        Country("Senegal", "SN", "+221", "🇸🇳", CountryName.SENEGAL),
        Country("Serbia", "RS", "+381", "🇷🇸", CountryName.SERBIA),
        Country("Seychelles", "SC", "+248", "🇸🇨", CountryName.SEYCHELLES),
        Country("Sierra Leone", "SL", "+232", "🇸🇱", CountryName.SIERRA_LEONE),
        Country("Singapore", "SG", "+65", "🇸🇬", CountryName.SINGAPORE),
        Country("Slovakia", "SK", "+421", "🇸🇰", CountryName.SLOVAKIA),
        Country("Slovenia", "SI", "+386", "🇸🇮", CountryName.SLOVENIA),
        Country("Solomon Islands", "SB", "+677", "🇸🇧", CountryName.SOLOMON_ISLANDS),
        Country("Somalia", "SO", "+252", "🇸🇴", CountryName.SOMALIA),
        Country("South Africa", "ZA", "+27", "🇿🇦", CountryName.SOUTH_AFRICA),
        Country("South Korea", "KR", "+82", "🇰🇷", CountryName.SOUTH_KOREA),
        Country("South Sudan", "SS", "+211", "🇸🇸", CountryName.SOUTH_SUDAN),
        Country("Spain", "ES", "+34", "🇪🇸", CountryName.SPAIN),
        Country("Sri Lanka", "LK", "+94", "🇱🇰", CountryName.SRI_LANKA),
        Country("Sudan", "SD", "+249", "🇸🇩", CountryName.SUDAN),
        Country("Suriname", "SR", "+597", "🇸🇷", CountryName.SURINAME),
        Country("Sweden", "SE", "+46", "🇸🇪", CountryName.SWEDEN),
        Country("Switzerland", "CH", "+41", "🇨🇭", CountryName.SWITZERLAND),
        Country("Syria", "SY", "+963", "🇸🇾", CountryName.SYRIA),
        Country("Taiwan", "TW", "+886", "🇹🇼", CountryName.TAIWAN),
        Country("Tajikistan", "TJ", "+992", "🇹🇯", CountryName.TAJIKISTAN),
        Country("Tanzania", "TZ", "+255", "🇹🇿", CountryName.TANZANIA),
        Country("Thailand", "TH", "+66", "🇹🇭", CountryName.THAILAND),
        Country("Timor-Leste", "TL", "+670", "🇹🇱", CountryName.EAST_TIMOR),
        Country("Togo", "TG", "+228", "🇹🇬", CountryName.TOGO),
        Country("Tonga", "TO", "+676", "🇹🇴", CountryName.TONGA),
        Country("Trinidad and Tobago", "TT", "+1-868", "🇹🇹", CountryName.TRINIDAD_AND_TOBAGO),
        Country("Tunisia", "TN", "+216", "🇹🇳", CountryName.TUNISIA),
        Country("Turkey", "TR", "+90", "🇹🇷", CountryName.TURKEY),
        Country("Turkmenistan", "TM", "+993", "🇹🇲", CountryName.TURKMENISTAN),
        Country("Tuvalu", "TV", "+688", "🇹🇻", CountryName.TUVALU),
        Country("Uganda", "UG", "+256", "🇺🇬", CountryName.UGANDA),
        Country("Ukraine", "UA", "+380", "🇺🇦", CountryName.UKRAINE),
        Country("United Arab Emirates", "AE", "+971", "🇦🇪", CountryName.UNITED_ARAB_EMIRATES),
        Country("United Kingdom", "GB", "+44", "🇬🇧", CountryName.UNITED_KINGDOM),
        Country("United States", "US", "+1", "🇺🇸", CountryName.UNITED_STATES),
        Country("Uruguay", "UY", "+598", "🇺🇾", CountryName.URUGUAY),
        Country("Uzbekistan", "UZ", "+998", "🇺🇿", CountryName.UZBEKISTAN),

        Country("Vanuatu", "VU", "+678", "🇻🇺", CountryName.VANUATU),
        Country("Vatican City", "VA", "+39-06", "🇻🇦", CountryName.VATICAN_CITY),
        Country("Venezuela", "VE", "+58", "🇻🇪", CountryName.VENEZUELA),
        Country("Vietnam", "VN", "+84", "🇻🇳", CountryName.VIETNAM),
        Country("Yemen", "YE", "+967", "🇾🇪", CountryName.YEMEN),
        Country("Zambia", "ZM", "+260", "🇿🇲", CountryName.ZAMBIA),
        Country("Zimbabwe", "ZW", "+263", "🇿🇼", CountryName.ZIMBABWE)

    )
}

enum class CountryName {
    AFGHANISTAN, ALBANIA, ALGERIA, ANDORRA, ANGOLA, ANTIGUA_AND_BARBUDA, ARGENTINA, ARMENIA, AUSTRALIA, AUSTRIA, AZERBAIJAN, BAHAMAS, BAHRAIN, BANGLADESH, BARBADOS, BELARUS, BELGIUM, BELIZE, BENIN, BHUTAN, BOLIVIA, BOSNIA_AND_HERZEGOVINA, BOTSWANA, BRAZIL, BRUNEI, BULGARIA, BURKINA_FASO, BURUNDI, CAMBODIA, CAMEROON, CANADA, CAPE_VERDE, CENTRAL_AFRICAN_REPUBLIC, CHAD, CHILE, CHINA, COLOMBIA, COMOROS, CONGO, COSTA_RICA, CROATIA, CUBA, CYPRUS, CZECH_REPUBLIC, DEMOCRATIC_REPUBLIC_OF_THE_CONGO, DENMARK, DJIBOUTI, DOMINICA, DOMINICAN_REPUBLIC, EAST_TIMOR, ECUADOR, EGYPT, EL_SALVADOR, EQUATORIAL_GUINEA, ERITREA, ESTONIA, ETHIOPIA, FIJI, FINLAND, FRANCE, GABON, GAMBIA, GEORGIA, GERMANY, GHANA, GREECE, GRENADA, GUATEMALA, GUINEA, GUINEA_BISSAU, GUYANA, HAITI, HONDURAS, HUNGARY, ICELAND, INDIA, INDONESIA, IRAN, IRAQ, IRELAND, ISRAEL, ITALY, IVORY_COAST, JAMAICA, JAPAN, JORDAN, KAZAKHSTAN, KENYA, KIRIBATI, KOSOVO, KUWAIT, KYRGYZSTAN, LAOS, LATVIA, LEBANON, LESOTHO, LIBERIA, LIBYA, LIECHTENSTEIN, LITHUANIA, LUXEMBOURG, MADAGASCAR, MALAWI, MALAYSIA, MALDIVES, MALI, MALTA, MARSHALL_ISLANDS, MAURITANIA, MAURITIUS, MEXICO, MICRONESIA, MOLDOVA, MONACO, MONGOLIA, MONTENEGRO, MOROCCO, MOZAMBIQUE, MYANMAR, NAMIBIA, NAURU, NEPAL, NETHERLANDS, NEW_ZEALAND, NICARAGUA, NIGER, NIGERIA, NORTH_KOREA, NORTH_MACEDONIA, NORWAY, OMAN, PAKISTAN, PALAU, PALESTINE, PANAMA, PAPUA_NEW_GUINEA, PARAGUAY, PERU, PHILIPPINES, POLAND, PORTUGAL, QATAR, ROMANIA, RUSSIA, RWANDA, SAINT_KITTS_AND_NEVIS, SAINT_LUCIA, SAINT_VINCENT_AND_THE_GRENADINES, SAMOA, SAN_MARINO, SAO_TOME_AND_PRINCIPE, SAUDI_ARABIA, SENEGAL, SERBIA, SEYCHELLES, SIERRA_LEONE, SINGAPORE, SLOVAKIA, SLOVENIA, SOLOMON_ISLANDS, SOMALIA, SOUTH_AFRICA, SOUTH_KOREA, SOUTH_SUDAN, SPAIN, SRI_LANKA, SUDAN, SURINAME, SWAZILAND, SWEDEN, SWITZERLAND, SYRIA, TAIWAN, TAJIKISTAN, TANZANIA, THAILAND, TOGO, TONGA, TRINIDAD_AND_TOBAGO, TUNISIA, TURKEY, TURKMENISTAN, TUVALU, UGANDA, UKRAINE, UNITED_ARAB_EMIRATES, UNITED_KINGDOM, UNITED_STATES, URUGUAY, UZBEKISTAN, VANUATU, VATICAN_CITY, VENEZUELA, VIETNAM, YEMEN, ZAMBIA, ZIMBABWE
}












































