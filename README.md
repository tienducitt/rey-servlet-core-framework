# rey-servlet-core-framework

My core lib that I use in my management web app. (MySQL, Servlet, AngularJS)
### 1. Servlet
- Spring MVC-like annotations: @RController, @RServletMapping, @RResponseBody
- Servlet always response JSON(errCode, data, message), if you don't want to, just response by using HttpServletResponse object
- Throw RResponseException to quickly response a fail json result

### 2. Configuration
- Support read configs in INI format
- Default config directory: project_dir/con/[running_mode].config.ini (running_mode is zappprof system variable)
- import com.reydentx.core.config.RConfig <br>

### 3. MySQLClient
- implements Connection Pool.
- configures base on RConfig

### 4. Log4j configuration file
Different log levels to different files for all logger. See /conf/log4j.properties
