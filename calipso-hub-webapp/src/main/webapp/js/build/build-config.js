({
	include: ['main'],
   baseUrl: "../../js",
   out: '../../../../../target/requirejs-optimized/js/main.js',
    mainConfigFile: "../main.js",

    wrapShim:               true,
    removeCombined:         true,
    findNestedDependencies: true, 

    fileExclusionRegExp: /^build/,
    

    inlineText: true,
    paths:{
   	'handlebars-compiler' : 'lib/handlebars' 
    },
})