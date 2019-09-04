package cn.celess.blog.exception;

import cn.celess.blog.enmu.ResponseEnum;
import cn.celess.blog.entity.Response;
import cn.celess.blog.service.MailService;
import cn.celess.blog.util.DateFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : xiaohai
 * @date : 2019/03/28 17:02
 */

@ControllerAdvice
public class ExceptionHandle {
    @Autowired
    MailService mailService;
    @Autowired
    HttpServletRequest request;
    public static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @Value("${spring.profiles.active}")
    private String activeModel;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Response handle(Exception e) {
        //自定义错误
        if (e instanceof MyException) {
            logger.debug("返回了自定义的exception,[code={},msg={}]", ((MyException) e).getCode(), e.getMessage());
            return new Response(((MyException) e).getCode(), e.getMessage(), null, System.currentTimeMillis());
        }
        //请求路径不支持该方法
        if (e instanceof HttpRequestMethodNotSupportedException) {
            logger.debug("遇到请求路径与请求方法不匹配的请求，[msg={}，path:{},method:{}]", e.getMessage(),request.getRequestURL(),request.getMethod());
            return new Response(ResponseEnum.ERROR.getCode(), e.getMessage(), null, System.currentTimeMillis());
        }
        //数据输入类型不匹配
        if (e instanceof MethodArgumentTypeMismatchException) {
            logger.debug("输入类型不匹配,[msg={}]", e.getMessage());
            return new Response(ResponseEnum.PARAMETERS_ERROR.getCode(), "数据输入有问题，请修改后再访问", null, System.currentTimeMillis());
        }
        //数据验证失败
        if (e instanceof BindException) {
            logger.debug("数据验证失败,[msg={}]", e.getMessage());
            return new Response(ResponseEnum.PARAMETERS_ERROR.getCode(), "数据输入有问题，请修改", null, System.currentTimeMillis());
        }
        //数据输入不完整
        if (e instanceof MissingServletRequestParameterException) {
            logger.debug("数据输入不完整,[msg={}]", e.getMessage());
            return new Response(ResponseEnum.PARAMETERS_ERROR.getCode(), "数据输入不完整,请检查", null, System.currentTimeMillis());
        }

        // 发送错误信息到邮箱
        if ("prod".equals(activeModel)) {
            logger.debug("有一个未捕获的bug，已发送到邮箱");
            sendMessage(e);
        }
        e.printStackTrace();
        return new Response(ResponseEnum.ERROR.getCode(), "服务器出现错误，已记录", null, System.currentTimeMillis());
    }

    /**
     * 发送错误信息
     *
     * @param e 错误
     */
    private void sendMessage(Exception e) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("a@celess.cn");
        simpleMailMessage.setSubject("服务器出现了错误");
        StringBuilder msg = new StringBuilder();
        msg.append("requirePath:\n").append(request.getRequestURL().toString()).append("?").append(request.getQueryString()).append("\n\n\n");
        msg.append("msg:\n").append(e.getMessage()).append("\n\n\n");
        msg.append("date:\n").append(DateFormatUtil.getNow()).append("\n\n\n");
        msg.append("from:\n").append(request.getHeader("User-Agent")).append("\n\n\n");
        msg.append("ip:\n").append(request.getRemoteAddr()).append("\n\n\n");
        simpleMailMessage.setText(msg.toString());
        mailService.AsyncSend(simpleMailMessage);
    }

}
