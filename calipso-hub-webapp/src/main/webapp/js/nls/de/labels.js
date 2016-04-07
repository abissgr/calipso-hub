define({
	"intlTelInput" : {
		"eg" : "e.g.",
		"DEFAULT" : 'Invalid number',
		"INVALID_COUNTRY_CODE" : 'Invalid country calling code',
		// This generally indicates the string passed in had less than 3 digits in it.
		// More specifically, the number failed to match the regular expression
		// VALID_PHONE_NUMBER.
		"NOT_A_NUMBER" : 'The string supplied did not seem to be a phone number',
		// This indicates the string, after any country calling code has been
		// stripped, had less digits than any valid phone number could have.
		"TOO_SHORT" : 'The string supplied is too short to be a phone number',
		// This indicates the string had more digits than any valid phone number could
		// have.
		"TOO_LONG" : 'The string supplied is too long to be a phone number'
	},

	"tmpl" : {
		"userRegistration" : {
			"titleNewPassword" : "Neues Passwort",
			"titleLogin" : "Benutzer Login",
			"titleNewAccount" : " Neues Konto eröffnen",
			"titleForgotPassword" : "Passwort vergessen?",
			"titleExpiredLink" : "Ist der Link ungültig geworden?",
			"formHelpNewPassword" : "Bitte erstellen Sie ein neues Passwort.",
			"formHelpLogin" : "Bitte geben Sie hier bitte Ihre Daten ein um sich anzumelden.",
			"formHelpNewAccount" : "Geben Sie bitte Ihren Namen und Emailadresse ein um ein Benutzerkonto zu erstellen und unser Anmeldeformular online auszufüllen.",
			"formHelpForgotPassword" : "Enter your email bellow to receive a password reset link in your inbox. The link will be valid for 24 hours.",
			"submitted" : "A verification email has been sent. Please check your inbox and click on the link to complete your registration. The link will be valid for 24 hours."
		},
	},// tmpl
	"calipso" : {
		"words" : {
			"close" : "Schließen",
			"step" : "Schritt",
			"yes" : "Ja",
			"no" : "Nein",
			"create" : "Create",
			"update" : "Update",
		},
		"select2" : {
			"placeholder": "Bitte auswählen",
		},
		"vatInput" : {
			"countryMissmatch" : "Die VAT Nummer stimmt nicht mit Ihrem ausgewählten Steuerland überein.",
			"invalidVat" : "Das Format der VAT Nummer is ungültig.",
		},//vatInput
	},//calipso
	"models" : {

		"users" : {
			"firstName" : {
				"search" : {
					"title" : "Vorname"
				},
				"default" : {
					"title" : "Vorname"
				}
			},
			"lastName" : {
				"search" : {
					"title" : "Nachname"
				},
				"default" : {
					"title" : "Nachname"
				}
			},
			"username" : {
				"search" : {
					"title" : "Benutzername"
				},
				"default" : {
					"title" : "Benutzername"
				}
			},
			"email" : {
				"search" : {
					"title" : "Email"
				},
				"default" : {
					"title" : "Email"
				}
			},
			"telephone" : {
				"default" : {
					"title" : "Telefon"
				}
			},
			"cellphone" : {
				"default" : {
					"title" : "Mobiltelefon"
				}
			},
			"active" : {
				"base" : {
					"title" : "Aktiv"
				},
				"create" : {
					"title" : "Aktiv",
					"help" : "Auswählen um Email Bestätigung zu überspringen"
				},
				"update" : {
					"title" : "Aktiv"
				}
			},
			"roles" : {
				"base" : {
					"title" : "Rollen"
				},
				"search" : {
					"title" : "Rollen"
				},
				"create" : {
					"title" : "Rollen"
				},
				"update" : {
					"title" : "Rollen"
				}
			}
		},
		"roles" : {
			"name" : {
				"search" : {
					"title" : "Name"
				},
				"default" : {
					"title" : "Name"
				}
			},
			"description" : {
				"search" : {
					"title" : "Beschreibung"
				},
				"default" : {
					"title" : "Beschreibung"
				}
			}
		},
		"countries" : {
			"name" : {
				"search" : {
					"title" : "Name"
				},
				"default" : {
					"title" : "Name"
				}
			},
			"nativeName" : {
				"search" : {
					"title" : "Lokaler Name"
				},
				"default" : {
					"title" : "Lokaler Name"
				}
			},
			"callingCode" : {
				"search" : {
					"title" : "Vorwahl"
				},
				"default" : {
					"title" : "Vorwahl"
				}
			},
			"capital" : {
				"search" : {
					"title" : "Hauptstadt"
				},
				"default" : {
					"title" : "Hauptstadt"
				}
			},
			"currency" : {
				"search" : {
					"title" : "Währung"
				},
				"default" : {
					"title" : "Währung"
				}
			},
			"languages" : {
				"search" : {
					"title" : "Sprachen"
				},
				"default" : {
					"title" : "Sprachen"
				}
			}
		},
		"userDetails" : {

			"id" : {
				"update" : {
					"title" : "Id"
				}
			},
			"isResetPasswordReguest" : {
				"update-createToken" : {
					"title" : "Is Reset Password Reguest"
				}
			},
			"email" : {
				"create" : {
					"title" : "Email"
				},
				"update-createToken" : {
					"title" : "Email"
				}
			},
			"resetPasswordToken" : {
				"create-withToken" : {
					"title" : "Reset Password Token",
				},
				"help" : {
					"title" : "Reset Password Token"
				}
			},
			"currentPassword" : {
				"update" : {
					"title" : "Aktuelles Passwort"
				}
			},
			"password" : {
				"default" : {
					"title" : "Passwort"
				},
				"update" : {
					"title" : "Neues Passwort"
				},
				"create-withToken" : {
					"title" : "Passwort"
				}
			},
			"passwordConfirmation" : {
				"update" : {
					"title" : "Passwort Bestätigung"
				},
				"create-withToken" : {
					"title" : "Passwort Bestätigung"
				}
			}
		},
		"userProfile" : {
			"firstName" : {
				"search" : {
					"title" : "Vorname"
				},
				"default" : {
					"title" : "Vorname"
				}
			},
			"lastName" : {
				"search" : {
					"title" : "Nachname"
				},
				"default" : {
					"title" : "Nachname"
				}
			},
			"username" : {
				"search" : {
					"title" : "Benutzername"
				},
				"default" : {
					"title" : "Benutzername"
				}
			},
			"email" : {
				"search" : {
					"title" : "Email"
				},
				"default" : {
					"title" : "Email"
				}
			},
			"telephone" : {
				"default" : {
					"title" : "Telefon"
				}
			},
			"cellphone" : {
				"default" : {
					"title" : "Mobiltelefon"
				}
			},
			"active" : {
				"base" : {
					"title" : "Aktiv"
				},
				"create" : {
					"title" : "Aktiv",
					"help" : "Auswählen um Email Bestätigiung zu überspringen"
				},
				"update" : {
					"title" : "Aktiv"
				}
			},
			"roles" : {
				"base" : {
					"title" : "Rollen"
				},
				"search" : {
					"title" : "Rollen"
				},
				"create" : {
					"title" : "Rollen"
				},
				"update" : {
					"title" : "Rollen"
				}
			}
		},
	},// models
	countries : {
		"AF" : "Afghanistan",
		"EG" : "Ägypten",
		"AX" : "Åland",
		"AL" : "Albanien",
		"DZ" : "Algerien",
		"AS" : "Amerikanisch-Samoa",
		"VI" : "Amerikanische Jungferninseln",
		"AD" : "Andorra",
		"AO" : "Angola",
		"AI" : "Anguilla",
		"AQ" : "Antarktika",
		"AG" : "Antigua und Barbuda",
		"GQ" : "Äquatorialguinea",
		"AR" : "Argentinien",
		"AM" : "Armenien",
		"AW" : "Aruba",
		"AZ" : "Aserbaidschan",
		"ET" : "Äthiopien",
		"AU" : "Australien",
		"BS" : "Bahamas",
		"BH" : "Bahrain",
		"BD" : "Bangladesch",
		"BB" : "Barbados",
		"BY" : "Belarus",
		"BE" : "Belgien",
		"BZ" : "Belize",
		"BJ" : "Benin",
		"BM" : "Bermuda",
		"BT" : "Bhutan",
		"BO" : "Bolivien",
		"BQ" : "Bonaire",
		"BA" : "Bosnien und Herzegowina",
		"BW" : "Botsuana",
		"BV" : "Bouvetinsel",
		"BR" : "Brasilien",
		"VG" : "Britische Jungferninseln",
		"IO" : "Britisches Territorium im Indischen Ozean",
		"BN" : "Brunei Darussalam",
		"BG" : "Bulgarien",
		"BF" : "Burkina Faso",
		"BI" : "Burundi",
		"CL" : "Chile",
		"CN" : "China",
		"CK" : "Cookinseln",
		"CR" : "Costa Rica",
		"CI" : "Côte d´Ivoire",
		"CW" : "Curacao",
		"DK" : "Dänemark",
		"DE" : "Deutschland",
		"DM" : "Dominica",
		"DO" : "Dominikanische Republik",
		"DJ" : "Dschibuti",
		"EC" : "Ecuador",
		"SV" : "El Salvador",
		"ER" : "Eritrea",
		"EE" : "Estland",
		"FK" : "Falklandinseln",
		"FO" : "Färöer",
		"FJ" : "Fidschi",
		"FI" : "Finnland",
		"FR" : "Frankreich",
		"GF" : "Französisch-Guayana",
		"PF" : "Französisch-Polynesien",
		"TF" : "Französische Süd- und Antarktisgebiete",
		"GA" : "Gabun",
		"GM" : "Gambia",
		"GE" : "Georgien",
		"GH" : "Ghana",
		"GI" : "Gibraltar",
		"GD" : "Grenada",
		"GR" : "Griechenland",
		"GL" : "Grönland",
		"GB" : "Großbritannien",
		"GP" : "Guadeloupe",
		"GU" : "Guam",
		"GT" : "Guatemala",
		"GG" : "Guernsey",
		"GW" : "Guinea-Bissau",
		"GN" : "Guinea",
		"GY" : "Guyana",
		"HT" : "Haiti",
		"HM" : "Heard und McDonaldinseln",
		"HN" : "Honduras",
		"HK" : "Hongkong",
		"IN" : "Indien",
		"ID" : "Indonesien",
		"IM" : "Insel Man",
		"IQ" : "Irak",
		"IR" : "Iran",
		"IE" : "Irland",
		"IS" : "Island",
		"IL" : "Israel",
		"IT" : "Italien",
		"JM" : "Jamaika",
		"JP" : "Japan",
		"YE" : "Jemen",
		"JE" : "Jersey",
		"JO" : "Jordanien",
		"KY" : "Kaimaninseln",
		"KH" : "Kambodscha",
		"CM" : "Kamerun",
		"CA" : "Kanada",
		"CV" : "Kap Verde",
		"KZ" : "Kasachstan",
		"QA" : "Katar",
		"KE" : "Kenia",
		"KG" : "Kirgisistan",
		"KI" : "Kiribati",
		"UM" : "Kleinere Amerikanische Überseeinseln",
		"CC" : "Kokosinseln (Keelinginseln)",
		"CO" : "Kolumbien",
		"KM" : "Komoren",
		"CD" : "Kongo, Demokratische Republik",
		"CG" : "Kongo",
		"KP" : "Korea, Demokratische Volksrepublik",
		"KR" : "Korea, Republik",
		"XK" : "Kosovo",
		"HR" : "Kroatien",
		"CU" : "Kuba",
		"KW" : "Kuwait",
		"LA" : "Laos",
		"LS" : "Lesotho",
		"LV" : "Lettland",
		"LB" : "Libanon",
		"LR" : "Liberia",
		"LY" : "Libyen",
		"LI" : "Liechtenstein",
		"LT" : "Litauen",
		"LU" : "Luxemburg",
		"MO" : "Macau",
		"MG" : "Madagaskar",
		"MW" : "Malawi",
		"MY" : "Malaysia",
		"MV" : "Malediven",
		"ML" : "Mali",
		"MT" : "Malta",
		"MA" : "Marokko",
		"MH" : "Marshallinseln",
		"MQ" : "Martinique",
		"MR" : "Mauretanien",
		"MU" : "Mauritius",
		"YT" : "Mayotte",
		"MK" : "Mazedonien",
		"MX" : "Mexiko",
		"FM" : "Mikronesien",
		"MD" : "Moldau",
		"MC" : "Monaco",
		"MN" : "Mongolei",
		"ME" : "Montenegro",
		"MS" : "Montserrat",
		"MZ" : "Mosambik",
		"MM" : "Myanmar",
		"NA" : "Namibia",
		"NR" : "Nauru",
		"NP" : "Nepal",
		"NC" : "Neukaledonien",
		"NZ" : "Neuseeland",
		"NI" : "Nicaragua",
		"NL" : "Niederlande",
		"NE" : "Niger",
		"NG" : "Nigeria",
		"NU" : "Niue",
		"MP" : "Nördliche Marianen",
		"NF" : "Norfolkinsel",
		"NO" : "Norwegen",
		"OM" : "Oman",
		"AT" : "Österreich",
		"PK" : "Pakistan",
		"PW" : "Palau",
		"PS" : "Palestina",
		"PA" : "Panama",
		"PG" : "Papua-Neuguinea",
		"PY" : "Paraguay",
		"PE" : "Peru",
		"PH" : "Philippinen",
		"PN" : "Pitcairninseln",
		"PL" : "Polen",
		"PT" : "Portugal",
		"PR" : "Puerto Rico",
		"RE" : "Réunion",
		"RW" : "Ruanda",
		"RO" : "Rumänien",
		"RU" : "Russische Föderation",
		"MF" : "Saint-Martin",
		"SB" : "Salomonen",
		"ZM" : "Sambia",
		"WS" : "Samoa",
		"SM" : "San Marino",
		"ST" : "São Tomé und Príncipe",
		"SA" : "Saudi-Arabien",
		"SE" : "Schweden",
		"CH" : "Schweiz",
		"SN" : "Senegal",
		"RS" : "Serbien",
		"SC" : "Seychellen",
		"SL" : "Sierra Leone",
		"ZW" : "Simbabwe",
		"SG" : "Singapur",
		"SX" : "Sint Maarten",
		"SK" : "Slowakei",
		"SI" : "Slowenien",
		"SO" : "Somalia",
		"ES" : "Spanien",
		"SJ" : "Spitzbergen",
		"LK" : "Sri Lanka",
		"BL" : "St. Barthélemy",
		"SH" : "St. Helena, Ascension und Tristan da Cunha",
		"KN" : "St. Kitts und Nevis",
		"LC" : "St. Lucia",
		"PM" : "St. Pierre und Miquelon",
		"VC" : "St. Vincent und die Grenadinen",
		"ZA" : "Südafrika",
		"SD" : "Sudan",
		"GS" : "Südgeorgien und die Südlichen Sandwichinseln",
		"SS" : "Südsudan",
		"SR" : "Suriname",
		"SZ" : "Swasiland",
		"SY" : "Syrien",
		"TJ" : "Tadschikistan",
		"TW" : "Taiwan",
		"TZ" : "Tansania",
		"TH" : "Thailand",
		"TL" : "Timor-Leste",
		"TG" : "Togo",
		"TK" : "Tokelau",
		"TO" : "Tonga",
		"TT" : "Trinidad und Tobago",
		"TD" : "Tschad",
		"CZ" : "Tschechische Republik",
		"TN" : "Tunesien",
		"TR" : "Türkei",
		"TM" : "Turkmenistan",
		"TC" : "Turks- und Caicosinseln",
		"TV" : "Tuvalu",
		"UG" : "Uganda",
		"UA" : "Ukraine",
		"HU" : "Ungarn",
		"UY" : "Uruguay",
		"UZ" : "Usbekistan",
		"VU" : "Vanuatu",
		"VA" : "Vatikanstadt",
		"VE" : "Venezuela",
		"AE" : "Vereinigte Arabische Emirate",
		"US" : "Vereinigte Staaten",
		"VN" : "Vietnam",
		"WF" : "Wallis und Futuna",
		"CX" : "Weihnachtsinsel",
		"EH" : "Westsahara",
		"CF" : "Zentralafrikanische Republik",
		"CY" : "Zypern"
	}
});
