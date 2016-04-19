package org.jboss.set.overview.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.set.assistant.data.ProcessorData;
import org.jboss.set.overview.ejb.Aider;

@WebServlet(name = "PayloadOverviewServlet", loadOnStartup = 1, urlPatterns = { "/payloadoverview" })
public class PayloadOverviewServlet extends HttpServlet {

    private static final long serialVersionUID = 8833071859201802046L;

    public static Logger logger = Logger.getLogger(PayloadOverviewServlet.class.getCanonicalName());

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private List<ProcessorData> payloadData = new ArrayList<>();

    @EJB
    private Aider aiderService;

    public PayloadOverviewServlet() {
        super();
    }

    @Override
    public void init() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                logger.log(Level.INFO, "payload data initialisation in Servlet init()");
                aiderService.generatePayloadData();
            }
        });
        executorService.shutdown();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Put the data list in request and let Freemarker paint it.
        payloadData = Aider.getPayloadData();
        if (payloadData == null || payloadData.isEmpty()) {
            response.addHeader("Refresh", "5");
            request.getRequestDispatcher("/error.html").forward(request, response);
        } else {
            request.setAttribute("rows", payloadData);
            request.getRequestDispatcher("/payload.ftl").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // do nothing
    }
}