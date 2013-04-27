package com.tabs.phototab.util.http;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

public class Http {

    public static String soap(String urlString, String request) throws Exception {
        return soap(urlString, "text/xml", request);

    }

    public static String soap(String urlString, String conttype, String request, boolean ignoreSSL) throws Exception {
        if (ignoreSSL) {
            try {
                SSLContext context = SSLContext.getInstance("SSLv3");
                TrustManager[] trustManagerArray = {new NullX509TrustManager()};
                context.init(null, trustManagerArray, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
            } catch (Exception e) {
            }
        }
        return soap(urlString, conttype, request);
    }

    public static String soap(String urlString, String conttype, String request) throws Exception {
        long start = System.currentTimeMillis();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", conttype);

        Writer wout = new OutputStreamWriter(conn.getOutputStream());
        wout.write(request);
        wout.flush();
        wout.close();

        long time = System.currentTimeMillis() - start;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Сервис недоступен</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Ресурс не найден</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Неправильный запрос</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Метод не разрешен для данного ресурса</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Ошибка в работе ресурса</error>";
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return "";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String nextLine = null;
        StringBuilder sb = new StringBuilder();
        while ((nextLine = in.readLine()) != null) {
            sb.append(nextLine).append("\n");
        }
        return sb.toString();
    }

    public static String soapTimeout(String urlString, String conttype, String request, int timeout) throws Exception {

        long start = System.currentTimeMillis();
        
        URL url = new URL((URL) null, urlString, new HttpTimeoutHandler(timeout));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", conttype);

        Writer wout = new OutputStreamWriter(conn.getOutputStream());
        wout.write(request);
        wout.flush();
        wout.close();
        long time = System.currentTimeMillis() - start;

        if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Сервис временно не доступен, попробуйте еще раз или повторите операцию позже</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Ресурс не найден</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Неправильный запрос</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Метод не разрешен для данного ресурса</error>";
        } else if (conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><error>Ошибка в работе ресурса</error>";
        }
        if ((conn.getResponseCode() != HttpURLConnection.HTTP_OK) && (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED)) {
            return "";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String nextLine = null;
        StringBuilder sb = new StringBuilder();
        while ((nextLine = in.readLine()) != null) {
            sb.append(nextLine).append("\n");
        }
        return sb.toString();
    }

    public static String soap(String urlString, String request, boolean IgnoreSSL) throws Exception {
        return soap(urlString, request, IgnoreSSL, null);
    }

    public static String soap(String urlString, String request, boolean IgnoreSSL, String coding) throws Exception {
        return soap(urlString, request, IgnoreSSL, coding, "", Integer.parseInt("3"), Integer.parseInt("20"));
    }

    public static String soap(String urlString, String request, boolean IgnoreSSL, String coding, String ErrorMessage, int connectTimeout, int readTimeout) throws Exception {

        long start = System.currentTimeMillis();

        URL url = new URL(urlString);
        StringBuilder sb = new StringBuilder();
        if (IgnoreSSL) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            try {
                // Let us create the factory where we can set some parameters for the connection
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            } catch (java.security.NoSuchAlgorithmException nsae) {

            } catch (java.security.KeyManagementException kme) {

            }
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(connectTimeout * 1000);
            conn.setReadTimeout(readTimeout * 1000);

            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "text/xml");

            Writer wout = new OutputStreamWriter(conn.getOutputStream());
            wout.write(request);
            wout.flush();
            wout.close();

            long time = System.currentTimeMillis() - start;


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }
            BufferedReader in = null;
            if (coding == null) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), coding));
            }
            String nextLine = "";
            while ((nextLine = in.readLine()) != null) {
                sb.append(nextLine).append("\n");
            }
            in.close();

        } catch (MalformedURLException mue) {

        } catch (java.net.UnknownHostException uhe) {

        } catch (IOException ioe) {
            if (!ErrorMessage.isEmpty()) {


            }

        }


        return sb.toString();
    }


    public static String Request(String requestURL) throws Exception {
        return Request(requestURL, "GET", "");
    }

    public static String Request(String requestURL, String METHOD) throws Exception {
        return Request(requestURL, METHOD, "");
    }

    public static String Request(String requestURL, String METHOD, String data) throws Exception {
        return Request(requestURL, METHOD, data, "KOI8_R");
    }

    public static String Request(String requestURL, String METHOD, String data, String inputEncoding) throws Exception {
        long start = System.currentTimeMillis();


        URL url = new URL(requestURL);
        StringBuilder sb = new StringBuilder();
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (java.security.NoSuchAlgorithmException nsae) {

        } catch (java.security.KeyManagementException kme) {

        }
        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(METHOD);
            if ("POST".equals(METHOD)) {
                Writer wout = new OutputStreamWriter(conn.getOutputStream());
                wout.write(data);
                wout.flush();
                wout.close();
            }

            long time = System.currentTimeMillis() - start;


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), inputEncoding));
            String nextLine = null;
            while ((nextLine = in.readLine()) != null) {
                sb.append(nextLine).append("\n");
            }
        } catch (MalformedURLException mue) {

        } catch (java.net.UnknownHostException uhe) {

        } catch (IOException ioe) {

        }


        return sb.toString();
    }


    public static String Request(String requestURL, String METHOD, String data, String inputEncoding, int connectTimeout, int readTimeout) throws Exception {
        long start = System.currentTimeMillis();


        URL url = new URL(requestURL);
        StringBuilder sb = new StringBuilder();
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (java.security.NoSuchAlgorithmException nsae) {

        } catch (java.security.KeyManagementException kme) {

        }
        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(connectTimeout*1000);
            conn.setReadTimeout(readTimeout*1000);

            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(METHOD);
            if ("POST".equals(METHOD)) {
                Writer wout = new OutputStreamWriter(conn.getOutputStream());
                wout.write(data);
                wout.flush();
                wout.close();
            }

            long time = System.currentTimeMillis() - start;


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), inputEncoding));
            String nextLine = null;
            while ((nextLine = in.readLine()) != null) {
                sb.append(nextLine).append("\n");
            }
        } catch (MalformedURLException mue) {

        } catch (java.net.UnknownHostException uhe) {

        } catch (IOException ioe) {

        }


        return sb.toString();
    }

    public static String sendCurl(String url, String request) {
        StringBuilder resp = new StringBuilder();
        try {
            String command = "curl -k -d " + request + " --connect-timeout 50 -m 50 " + url;

            Process pr = Runtime.getRuntime().exec(command);
            String line = null;
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line != null) {
                    resp.append(line);
                }
            }
            input.close();
            pr.waitFor();

        } catch (Exception e) {

        }
        return resp.toString();
    }

    public static String RequestTimeout(String requestURL, String METHOD, String data, String inputEncoding, int connTimeout, int readTimeout) throws MalformedURLException, IOException /*throws Exception*/ {

        long start = System.currentTimeMillis();


        URL url = new URL(requestURL);
        StringBuilder sb = new StringBuilder();
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (java.security.NoSuchAlgorithmException nsae) {
            
        } catch (java.security.KeyManagementException kme) {
            
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod(METHOD);
        conn.setConnectTimeout(connTimeout * 1000);
        conn.setReadTimeout(readTimeout * 1000);

        if ("POST".equals(METHOD)) {
            Writer wout = new OutputStreamWriter(conn.getOutputStream());
            wout.write(data);
            wout.flush();
            wout.close();
        }

        long time = System.currentTimeMillis() - start;
        

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), inputEncoding));
        String nextLine = null;
        while ((nextLine = in.readLine()) != null) {
            sb.append(nextLine).append("\n");
        }

        
        return sb.toString();
    }

    public static class NullX509TrustManager implements X509TrustManager { /* Implements all methods, keeping them empty or returning null */


        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }

    public static class NullHostnameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}