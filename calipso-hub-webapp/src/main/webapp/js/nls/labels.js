define({
	"root" : {
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
				"titleNewPassword" : "New Password",
				"titleLogin" : "User Login",
				"titleNewAccount" : " Create New Account",
				"titleForgotPassword" : "Forgot Password?",
				"titleExpiredLink" : "Expired Link?",
				"formHelpNewPassword" : "Please create a new password below.",
				"formHelpLogin" : "Enter your credentials below to login.",
				"formHelpNewAccount" : "Fill in your name and email address below to create a new account.",
				"formHelpForgotPassword" : "Enter your email bellow to receive a password reset link in your inbox. The link will be valid for 24 hours.",
				"submitted" : "A verification email has been sent. Please check your inbox and click on the link to complete your registration. The link will be valid for 24 hours."
			},
			
		},// tmpl
		"calipso" : {
			"words" : {
				"close" : "Close",
				"step" : "Step",
				"yes" : "Yes",
				"no" : "No"
			},
			"select2" : {
				"placeholder": "Type to select",
			},
			"vatInput" : {
				"countryMissmatch" : "VAT number doesn't match the country code. ",
				"invalidVat" : "The VAT number format is invalid.",
			},//vatInput
		},//calipso
		"models" : {

			"users" : {
				"firstName" : {
					"search" : {
						"title" : "First Name"
					},
					"default" : {
						"title" : "First Name"
					}
				},
				"lastName" : {
					"search" : {
						"title" : "Last Name"
					},
					"default" : {
						"title" : "Last Name"
					}
				},
				"username" : {
					"search" : {
						"title" : "Username"
					},
					"default" : {
						"title" : "Username"
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
						"title" : "Telephone"
					}
				},
				"cellphone" : {
					"default" : {
						"title" : "Cellphone"
					}
				},
				"active" : {
					"base" : {
						"title" : "Active"
					},
					"create" : {
						"title" : "Active",
						"help" : "Select to skip email confirmation"
					},
					"update" : {
						"title" : "Active"
					}
				},
				"roles" : {
					"base" : {
						"title" : "Roles"
					},
					"search" : {
						"title" : "Roles"
					},
					"create" : {
						"title" : "Roles"
					},
					"update" : {
						"title" : "Roles"
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
						"title" : "Description"
					},
					"default" : {
						"title" : "Description"
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
						"title" : "Native Name"
					},
					"default" : {
						"title" : "Native Name"
					}
				},
				"callingCode" : {
					"search" : {
						"title" : "Calling Code"
					},
					"default" : {
						"title" : "Calling Code"
					}
				},
				"capital" : {
					"search" : {
						"title" : "Capital"
					},
					"default" : {
						"title" : "Capital"
					}
				},
				"currency" : {
					"search" : {
						"title" : "Currency"
					},
					"default" : {
						"title" : "Currency"
					}
				},
				"languages" : {
					"search" : {
						"title" : "Languages"
					},
					"default" : {
						"title" : "Languages"
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
						"title" : "Reset Password Token"
					},
					"help" : {
						"title" : "Reset Password Token"
					}
				},
				"currentPassword" : {
					"update" : {
						"title" : "Current Password"
					}
				},
				"password" : {
					"create" : {
						"title" : "Password"
					},
					"update" : {
						"title" : "New Password"
					},
					"create-withToken" : {
						"title" : "Password"
					}
				},
				"passwordConfirmation" : {
					"update" : {
						"title" : "Password Confirmation"
					},
					"create-withToken" : {
						"title" : "Password Confirmation"
					}
				}
			},
			"userProfile" : {
				"firstName" : {
					"search" : {
						"title" : "First Name"
					},
					"default" : {
						"title" : "First Name"
					}
				},
				"lastName" : {
					"search" : {
						"title" : "Last Name"
					},
					"default" : {
						"title" : "Last Name"
					}
				},
				"username" : {
					"search" : {
						"title" : "Username"
					},
					"default" : {
						"title" : "Username"
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
						"title" : "Telephone"
					}
				},
				"cellphone" : {
					"default" : {
						"title" : "Cellphone"
					}
				},
				"active" : {
					"base" : {
						"title" : "Active"
					},
					"create" : {
						"title" : "Active",
						"help" : "Select to skip email confirmation"
					},
					"update" : {
						"title" : "Active"
					}
				},
				"roles" : {
					"base" : {
						"title" : "Roles"
					},
					"search" : {
						"title" : "Roles"
					},
					"create" : {
						"title" : "Roles"
					},
					"update" : {
						"title" : "Roles"
					}
				}
			},
		},// models
		countries : {
			"AD" : "Andorra",
			"AE" : "United Arab Emirates",
			"AF" : "Afghanistan",
			"AG" : "Antigua and Barbuda",
			"AI" : "Anguilla",
			"AL" : "Albania",
			"AM" : "Armenia",
			"AO" : "Angola",
			"AQ" : "Antarctica",
			"AR" : "Argentina",
			"AS" : "American Samoa",
			"AT" : "Austria",
			"AU" : "Australia",
			"AW" : "Aruba",
			"AX" : "Åland",
			"AZ" : "Azerbaijan",
			"BA" : "Bosnia and Herzegovina",
			"BB" : "Barbados",
			"BD" : "Bangladesh",
			"BE" : "Belgium",
			"BF" : "Burkina Faso",
			"BG" : "Bulgaria",
			"BH" : "Bahrain",
			"BI" : "Burundi",
			"BJ" : "Benin",
			"BL" : "Saint Barthélemy",
			"BM" : "Bermuda",
			"BN" : "Brunei",
			"BO" : "Bolivia",
			"BQ" : "Bonaire",
			"BR" : "Brazil",
			"BS" : "Bahamas",
			"BT" : "Bhutan",
			"BV" : "Bouvet Island",
			"BW" : "Botswana",
			"BY" : "Belarus",
			"BZ" : "Belize",
			"CA" : "Canada",
			"CC" : "Cocos [Keeling] Islands",
			"CD" : "Democratic Republic of the Congo",
			"CF" : "Central African Republic",
			"CG" : "Republic of the Congo",
			"CH" : "Switzerland",
			"CI" : "Ivory Coast",
			"CK" : "Cook Islands",
			"CL" : "Chile",
			"CM" : "Cameroon",
			"CN" : "China",
			"CO" : "Colombia",
			"CR" : "Costa Rica",
			"CU" : "Cuba",
			"CV" : "Cape Verde",
			"CW" : "Curacao",
			"CX" : "Christmas Island",
			"CY" : "Cyprus",
			"CZ" : "Czechia",
			"DE" : "Germany",
			"DJ" : "Djibouti",
			"DK" : "Denmark",
			"DM" : "Dominica",
			"DO" : "Dominican Republic",
			"DZ" : "Algeria",
			"EC" : "Ecuador",
			"EE" : "Estonia",
			"EG" : "Egypt",
			"EH" : "Western Sahara",
			"ER" : "Eritrea",
			"ES" : "Spain",
			"ET" : "Ethiopia",
			"FI" : "Finland",
			"FJ" : "Fiji",
			"FK" : "Falkland Islands",
			"FM" : "Micronesia",
			"FO" : "Faroe Islands",
			"FR" : "France",
			"GA" : "Gabon",
			"GB" : "United Kingdom",
			"GD" : "Grenada",
			"GE" : "Georgia",
			"GF" : "French Guiana",
			"GG" : "Guernsey",
			"GH" : "Ghana",
			"GI" : "Gibraltar",
			"GL" : "Greenland",
			"GM" : "Gambia",
			"GN" : "Guinea",
			"GP" : "Guadeloupe",
			"GQ" : "Equatorial Guinea",
			"GR" : "Greece",
			"GS" : "South Georgia and the South Sandwich Islands",
			"GT" : "Guatemala",
			"GU" : "Guam",
			"GW" : "Guinea-Bissau",
			"GY" : "Guyana",
			"HK" : "Hong Kong",
			"HM" : "Heard Island and McDonald Islands",
			"HN" : "Honduras",
			"HR" : "Croatia",
			"HT" : "Haiti",
			"HU" : "Hungary",
			"ID" : "Indonesia",
			"IE" : "Ireland",
			"IL" : "Israel",
			"IM" : "Isle of Man",
			"IN" : "India",
			"IO" : "British Indian Ocean Territory",
			"IQ" : "Iraq",
			"IR" : "Iran",
			"IS" : "Iceland",
			"IT" : "Italy",
			"JE" : "Jersey",
			"JM" : "Jamaica",
			"JO" : "Jordan",
			"JP" : "Japan",
			"KE" : "Kenya",
			"KG" : "Kyrgyzstan",
			"KH" : "Cambodia",
			"KI" : "Kiribati",
			"KM" : "Comoros",
			"KN" : "Saint Kitts and Nevis",
			"KP" : "North Korea",
			"KR" : "South Korea",
			"KW" : "Kuwait",
			"KY" : "Cayman Islands",
			"KZ" : "Kazakhstan",
			"LA" : "Laos",
			"LB" : "Lebanon",
			"LC" : "Saint Lucia",
			"LI" : "Liechtenstein",
			"LK" : "Sri Lanka",
			"LR" : "Liberia",
			"LS" : "Lesotho",
			"LT" : "Lithuania",
			"LU" : "Luxembourg",
			"LV" : "Latvia",
			"LY" : "Libya",
			"MA" : "Morocco",
			"MC" : "Monaco",
			"MD" : "Moldova",
			"ME" : "Montenegro",
			"MF" : "Saint Martin",
			"MG" : "Madagascar",
			"MH" : "Marshall Islands",
			"MK" : "Macedonia",
			"ML" : "Mali",
			"MM" : "Myanmar [Burma]",
			"MN" : "Mongolia",
			"MO" : "Macao",
			"MP" : "Northern Mariana Islands",
			"MQ" : "Martinique",
			"MR" : "Mauritania",
			"MS" : "Montserrat",
			"MT" : "Malta",
			"MU" : "Mauritius",
			"MV" : "Maldives",
			"MW" : "Malawi",
			"MX" : "Mexico",
			"MY" : "Malaysia",
			"MZ" : "Mozambique",
			"NA" : "Namibia",
			"NC" : "New Caledonia",
			"NE" : "Niger",
			"NF" : "Norfolk Island",
			"NG" : "Nigeria",
			"NI" : "Nicaragua",
			"NL" : "Netherlands",
			"NO" : "Norway",
			"NP" : "Nepal",
			"NR" : "Nauru",
			"NU" : "Niue",
			"NZ" : "New Zealand",
			"OM" : "Oman",
			"PA" : "Panama",
			"PE" : "Peru",
			"PF" : "French Polynesia",
			"PG" : "Papua New Guinea",
			"PH" : "Philippines",
			"PK" : "Pakistan",
			"PL" : "Poland",
			"PM" : "Saint Pierre and Miquelon",
			"PN" : "Pitcairn Islands",
			"PR" : "Puerto Rico",
			"PS" : "Palestine",
			"PT" : "Portugal",
			"PW" : "Palau",
			"PY" : "Paraguay",
			"QA" : "Qatar",
			"RE" : "Réunion",
			"RO" : "Romania",
			"RS" : "Serbia",
			"RU" : "Russia",
			"RW" : "Rwanda",
			"SA" : "Saudi Arabia",
			"SB" : "Solomon Islands",
			"SC" : "Seychelles",
			"SD" : "Sudan",
			"SE" : "Sweden",
			"SG" : "Singapore",
			"SH" : "Saint Helena",
			"SI" : "Slovenia",
			"SJ" : "Svalbard and Jan Mayen",
			"SK" : "Slovakia",
			"SL" : "Sierra Leone",
			"SM" : "San Marino",
			"SN" : "Senegal",
			"SO" : "Somalia",
			"SR" : "Suriname",
			"SS" : "South Sudan",
			"ST" : "São Tomé and Príncipe",
			"SV" : "El Salvador",
			"SX" : "Sint Maarten",
			"SY" : "Syria",
			"SZ" : "Swaziland",
			"TC" : "Turks and Caicos Islands",
			"TD" : "Chad",
			"TF" : "French Southern Territories",
			"TG" : "Togo",
			"TH" : "Thailand",
			"TJ" : "Tajikistan",
			"TK" : "Tokelau",
			"TL" : "East Timor",
			"TM" : "Turkmenistan",
			"TN" : "Tunisia",
			"TO" : "Tonga",
			"TR" : "Turkey",
			"TT" : "Trinidad and Tobago",
			"TV" : "Tuvalu",
			"TW" : "Taiwan",
			"TZ" : "Tanzania",
			"UA" : "Ukraine",
			"UG" : "Uganda",
			"UM" : "U.S. Minor Outlying Islands",
			"US" : "United States",
			"UY" : "Uruguay",
			"UZ" : "Uzbekistan",
			"VA" : "Vatican City",
			"VC" : "Saint Vincent and the Grenadines",
			"VE" : "Venezuela",
			"VG" : "British Virgin Islands",
			"VI" : "U.S. Virgin Islands",
			"VN" : "Vietnam",
			"VU" : "Vanuatu",
			"WF" : "Wallis and Futuna",
			"WS" : "Samoa",
			"XK" : "Kosovo",
			"YE" : "Yemen",
			"YT" : "Mayotte",
			"ZA" : "South Africa",
			"ZM" : "Zambia",
			"ZW" : "Zimbabwe"
		}
	},
	"de" : true,
});
