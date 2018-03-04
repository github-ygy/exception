package com.ygy.exception.chain;


import com.ygy.exception.model.ExceptionParams;
import com.ygy.exception.model.ResultBean;
import com.ygy.exception.handler.ExceptionHandler;
import com.ygy.exception.handler.listener.ExceptionHandlerListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * Created by guoyao on 2018/2/25.
 * 异常处理链路
 */
@Component
@Scope("prototype")
public class ExceptionHandlerChain implements HandlerChain {

    private int cursor=0;

    @Resource
    ExceptionHandlerListener exceptionHandlerListener;


    public ResultBean doHandler(Exception e, ExceptionParams exceptionParams) {


        ArrayList<ExceptionHandler> handlers=exceptionHandlerListener.getHandlers();

        if (CollectionUtils.isEmpty(handlers)) {
            return ResultBean.ofError(exceptionParams);
        }

        if (cursor >= handlers.size()) {
            return ResultBean.ofError(exceptionParams,"系统异常,异常处理链路达到上限");
        }


        ResultBean resultBean=handlers.get(cursor++).handlerException(e, exceptionParams, this);

        if (resultBean == null) {
            resultBean = ResultBean.ofError(exceptionParams);
        }

        return resultBean;
    }
}