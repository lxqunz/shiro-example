package com.github.zhangkaitao.shiro.chapter7.web.servlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "advertisementServlet", urlPatterns = "/advertisement")
public class AdvertisementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Subject subject = SecurityUtils.getSubject();

        req.getRequestDispatcher("/WEB-INF/jsp/advertisement.jsp").forward(req, resp);

    }
}
