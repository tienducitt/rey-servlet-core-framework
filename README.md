# rey-servlet-core-framework

My core lib that I use in my management web app. (MySQL, Servlet, AngularJS)
### 1. Servlet
- @RController, @RServletMapping, @RResponseBody to quickly mapping servlet <br/>
- Servlet always response JSON(errCode, data, message), if you don't want to, you have to response by yourself
- Throw RResponseException to quickly response a fail json result

### 2. Configuration
- Support read configs in INI format
- Default config directory: project_dir/con/[running_mode].config.ini (running_mode is zappprof system variable)
- import com.reydentx.core.config.RConfig <br>

### 3. MySQLClient
- implement Connection Pool.
- configure base on RConfig

### 4. Log4j configuration file
Different log levels to different files for all logger. See /conf/log4j.properties
