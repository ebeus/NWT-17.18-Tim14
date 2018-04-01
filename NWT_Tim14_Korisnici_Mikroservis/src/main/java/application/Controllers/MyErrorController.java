package application.Controllers;

import application.Responses.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class MyErrorController  implements ErrorController{
    private static final String PATH="/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    ApiError error(HttpServletRequest request, HttpServletResponse response){

        RequestAttributes requestAttributes = new ServletRequestAttributes(request);

        return new ApiError(response.getStatus(),"ERROR","");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
