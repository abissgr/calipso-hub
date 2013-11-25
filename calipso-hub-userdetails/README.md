# Calipso Hub: UserDetails Module

Provides generic, stateless user detail services, including registration, authentication and authorization/ACL.

For an overview of what the module provides, consider the interfaces implemented by the `userDetailsService` 
provided in this module:

 - `gr.abiss.calipso.userDetails.service.UserDetailsService` (own signature interface)
 - `org.springframework.security.core.userdetails.UserDetailsService`
 - `org.springframework.social.security.SocialUserDetailsService`
 - `org.springframework.social.connect.ConnectionSignUp`
 - `org.springframework.social.connect.web.SignInAdapter`



## <a name="userDetailsConfig"> Get started: userDetailsConfig

Configuration options can be accessed through a `userDetailsConfig` bean implementation of 
`gr.abiss.calipso.userDetails.integration.UserDetailsConfig` interface, such as the included simple 
implementation:

```xml
    <bean id="userDetailsConfig" class="gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig">
	<constructor-arg value="${cookiesBasicAuthTokenName}" />
	<constructor-arg value="${cookiesDomain}" />
	<constructor-arg value="/" />
    </bean>
```

The arguments above correspond to header (or cookie, in case of JSONP) name, cookie domain and 
cookie path. Check out the javadoc for more options.


## <a name="userDetailsService"> Main dish: userDetailsService

One way to declare and adopt the default `userDetailsService` service provided with this module is by 
subclassing it. A subclass will automatically get picked up by Spring classpath scanning, if that 
is enabled and the package is in scope:


```java
    package my.package;

    import gr.abiss.calipso.userDetails.service.impl.UserDetailsServiceImpl;

    import javax.inject.Named;

    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;


    @Service
    @Named("userDetailsService")
    @Transactional(readOnly = true)
    public class MyUserDetailsService extends UserDetailsServiceImpl {
	// no need to override anything 
    }
```

Alternatively you can use the provided userDetailsService implementation directly:

```xml
    <bean id="userDetailsService" class="gr.abiss.calipso.userDetails.service.impl.UserDetailsServiceImpl" autowire="byType" />
```

Or by similarly declaring your custom subclass. The following dependencies will be autowired from the Spring beans in 
context during classpath scanning or must otherwise be provided in config:

 - [userDetailsConfig](#userDetailsConfig) (optional): an implementation of `gr.abiss.calipso.userDetails.integration.UserDetailsConfig`, see above
 - [localUserService](#localUserService) (required): An implementation of `gr.abiss.calipso.userDetails.integration.LocalUserService`, see bellow

### <a name="localUserService"> Adapter to user persistence:  localUserService

An implementation of this service adapter is required to integrate the declared `userDetailsService` bean with the service or repository 
used by your application for user (or any account) entity persistence.

You may find convinient implementing such `localUserService` on top of such a local user repository or service and simply provide an alias like

```xml
   <alias name="userService" alias="localUserService"/>
```

Hooking up a `localUserService` enables `userDetailsService` to handle requests for user login, signup, email address confirmation 
and password reset flows. The actual HTTP requests are intercepted by the controllers described bellow.


## <a name="userDetailsController"> Map to HTTP requests: The UserDetailsController

The available options for your Spring application to register the provided `UserDetailsController` class are the same as the ones described 
for the `userDetailsService` bean in the previous section above. More specifically you can A) subclass the controler and 
have it picked up by Spring classpath scanning:


```java
   package my.package;
	
   import gr.abiss.calipso.userDetails.controller.UserDetailsController;
	
   @Controller
   @RequestMapping(value = "/myapiauth", produces = { "application/json", "application/xml" })
   public class MyUserDetailsController extends UserDetailsController {
	   
            // no need to override anything unless you wish to change 
            // the default method RequestMappings/URLs
    }
```

and alternatively you can declare B) such a subclass or D) the provided implementation in your context configuration:

```xml
    <!--
        Your controller subclass or the one provided as
	gr.abiss.calipso.userDetails.controller.UserDetailsController
    -->
    <bean class="my.package.MyUserDetailsController" autowire="byType" />
```

The following bean dependencies will be autowired from the Spring beans in 
context during classpath scanning or must otherwise be provided in config:

 - [userDetailsConfig](#userDetailsConfig) (optional): an implementation of `gr.abiss.calipso.userDetails.integration.UserDetailsConfig`, see above
 - [UserDetailsService](#UserDetailsService) (required): the `gr.abiss.calipso.userDetails.service.UserDetailsService`, see bellow


### UserDetailsController RequestMappings

In the custom `MyUserDetailsController` subclass example above the base URL mapping path is set to "/myapiauth", overriding the
default "/apiauth" path prefix of the controller's method RequestMappin annotations. The remaining path components can be overriden 
with the same annotations per method accordingly. Here are the provided request mappings, assuming the default "/apiauth" base path is used:

 - GET/POST "/apiauth/confirmation/{token}": Assumes the request was triggered by a link found within an account confirmation email sent by your 
application. Completes the user registration by calling `localUserService.confirmPrincipal(token)`, then uses the returned `LocalUser` to 
login the corresponding user account. The request is redirected to the path returned by `localUser.getRedirectUrl()` if any is provided and 
the request method was a GET, or to the context path otherwise.

 - POST "/apiauth/password-reset-request/{userNameOrEmail}": Notifies your application a password reset was requested by calling 
`localUserService.handlePasswordResetRequest(userNameOrEmail)`. Your `localUserService' is responsible for produsing a token and using it 
in a password change form, see the next item for the form action URL.

 - POST "/apiauth/password-reset/{token}": Assumes the request was triggered by a password change form that submits a `UserDetails` as the request body. 
Persists the change by calling `localUserService.handlePasswordResetToken(userNameOrEmail, token, newPassword)`, then uses the returned `LocalUser` to 
login the corresponding user account. The generated response contains a UserDetails instance that coresponds to the logged in user.

 - GET "/apiauth/userDetails/remembered": Provides a "remember me" service that responds with the coresponding `UserDetails` based on a cookie.

 - POST "/apiauth/userDetails": Attempts to login the user based on the credentials (email/username, password) of the `UserDetails` found in the 
request body by calling `localUserService.findByCredentials(userNameOrEmail, password)`. Responds with a `UserDetails` instance that coresponds to the 
logged in user, if any. 

 - DELETE "/apiauth/userDetails" and 

 - POST "/apiauth/userDetails/logout": Logs out the user completely.
