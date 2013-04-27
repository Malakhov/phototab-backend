package com.tabs.phototab.service;

import com.tabs.phototab.util.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Malakhov
 * Date: 17.04.13
 * Time: 15:29
 */

@Component
@Path("/")
public class EntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    private static final String base = "https://api.vk.com/method/photos.getAlbums?uid=";

    private static final String auth = "https://oauth.vk.com/oauth/authorize?client_id=3587987&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fphoto%2Flist%2F1&response_type=&scope=12&state=&display=page";

    private static final String access_token = "https://oauth.vk.com/access_token";

    @GET
    @Path("/list/{id}")
    public Response list(@PathParam("id") String id){

        logger.info("EntryPoint::list::id = {}", id);

        String response = "";

        try {

            response = Http.Request(base + id);

        } catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        return Response.status(200).entity(response).build();
    }

    @GET
    @Path("/auth")
    public Response auth(final @Context HttpServletRequest request){

        logger.info("EntryPoint::auth");

        String response = "";

        try{

            String code = request.getParameter("code");

            String token = request.getParameter("access_token");
            String user_id = request.getParameter("user_id");

            logger.info("code = {}, token = {}, user_id = {}", new Object[] {code, token, user_id});

            if(code != null){
                response = Http.Request(access_token + "?code=" + request.getParameter("code") + "&client_secret=dJ1VvJh0coxg6setUu7y&client_id=3587987&redirect_uri=http://localhost:8080/api/photo/auth", "GET", "", "UTF-8");
            } else {

                logger.info("Pruff");

            }


            logger.info("response = {}", response);

        } catch (Exception ex){
            logger.error(ex.getMessage(), ex);
            response = "error";
        }

        return Response.status(200).entity(response).build();
    }

}
