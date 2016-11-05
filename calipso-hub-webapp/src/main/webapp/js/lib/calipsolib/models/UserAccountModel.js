/*
 * calipso-hub-webapp - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
define(['jquery', 'underscore', 'bloodhound', 'typeahead', "lib/calipsolib/util", "lib/calipsolib/form",
        "lib/calipsolib/uifield", "lib/calipsolib/backgrid", "lib/calipsolib/view", 'handlebars', "lib/calipsolib/models/UserModel"],
    function ($, _, Bloodhoud, Typeahead, Calipso, CalipsoForm, CalipsoField, CalipsoGrid, CalipsoView, Handlebars) {

        Calipso.model.UserAccountModel = Calipso.model.UserModel.extend(
            /** @lends Calipso.model.UserAccountModel.prototype */
            {
                toString: function () {
                    return this.get("username");
                }
                //urlRoot : "/api/rest/users"
            }, {
                // static members
                labelIcon: "fa fa-user fa-fw",
                menuConfig: null,
                public: true,
                pathFragment: "accounts",
                baseFragment: '/api/auth/',
                typeName: "Calipso.model.UserAccountModel",
                useCases: {
                    create: {
                        view: Calipso.view.UserRegistrationLayout,
                        fieldIncludes: Calipso.getConfigProperty("registration.forceCodes") ? ["firstName", "lastName", "email", "registrationCode"] : ["firstName", "lastName", "email"],
                        overrides: {
                            contentRegion: {
                                viewOptions: {
                                    template: Calipso.getTemplate("UseCaseCardFormView"),
                                    title: Calipso.util.getLabels("tmpl.userRegistration.titleNewAccount") +
                                    '<a href="#" class="btn btn-secondary btn-sm btn-social btn-facebook  pull-right" title="' +
                                    Calipso.util.getLabels("tmpl.login.fbLinkAlt") +
                                    '"><i class="fa fa-facebook-f"></i> ' + Calipso.util.getLabels("tmpl.login.fbLink") + '</a>',
                                    message: Calipso.util.getLabels("tmpl.userRegistration.formHelpNewAccount"),
                                    placeHolderLabelsOnly: true,
                                    formControlSize: "lg",
                                    submitButton: '<i class="fa fa-user-plus" aria-hidden="true"></i> ' + Calipso.util.getLabels("calipso.words.register")
                                },
                            },
                        },
                    },
                },
                fields: {
                    username: {
                        fieldType: "String",
                    },

                    firstName: {
                        fieldType: "String",
                    },
                    lastName: {
                        fieldType: "String",
                    },
                    email: {
                        fieldType: "Email",
                    },
                    registrationCode: {
                        fieldType: "String",
                    },
                    telephone: {
                        fieldType: "Tel",
                    },
                    cellphone: {
                        fieldType: "Tel",
                    },
                    active: {
                        fieldType: "Boolean",
                    },
                    /*roles : {
                     fieldType : "List",
                     //"form" : {
                     //	"listModel" : Calipso.model.RoleModel
                     //}
                     },*/
                },
            });

    });