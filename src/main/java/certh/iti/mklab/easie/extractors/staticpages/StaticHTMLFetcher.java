/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie.extractors.staticpages;

import certh.iti.mklab.easie.extractors.Fetcher;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import com.google.gson.Gson;
import org.apache.tika.parser.txt.CharsetDetector;
import org.bson.BsonString;
import org.bson.json.Converter;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author vasgat
 */
public class StaticHTMLFetcher extends Fetcher {

    private Document document;
    private int responseCode;
    private Connection connection;

    public StaticHTMLFetcher(String baseURL, String relativeURL) throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException {
        trustAllCertificates();
        connection = Jsoup.connect(new URI(baseURL + relativeURL).toASCIIString()).followRedirects(true).ignoreHttpErrors(true)
                .timeout(60000);
        document = connection.get();
        if ((baseURL + relativeURL).equals("https://elfar.cat/art/27043/el-amb-plantea-un-centenar-de-medidas-para-mejorar-la-movilidad-hasta-2024")) {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("C:\\Users\\vasgat\\Desktop\\site2.json"), StandardCharsets.UTF_8));

            org.bson.Document obj = new org.bson.Document();
            obj.append("results", document.text());
            writer.write(obj.toString());
            writer.close();
            Gson gson = new Gson();
            System.out.println(org.apache.commons.lang3.StringEscapeUtils.unescapeJava(gson.toJson(obj)));
        }
        responseCode = connection.response().statusCode();
    }

    public StaticHTMLFetcher(String fullURL) throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException {
        trustAllCertificates();
        connection = Jsoup.connect(new URI(fullURL).toASCIIString()).followRedirects(true).ignoreHttpErrors(true)
                .timeout(60000);
        document = connection.get();
        if ((fullURL).equals("https://elfar.cat/art/27043/el-amb-plantea-un-centenar-de-medidas-para-mejorar-la-movilidad-hasta-2024")) {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("C:\\Users\\vasgat\\Desktop\\site2.json"), StandardCharsets.UTF_8));
            org.bson.Document obj = new org.bson.Document();
            obj.append("results", document.text());
            writer.write(obj.toJson());
            writer.close();
            Gson gson = new Gson();
            System.out.println(org.apache.commons.lang3.StringEscapeUtils.unescapeJava(gson.toJson(obj)));
        }
        responseCode = connection.response().statusCode();
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public Document getHTMLDocument() {
        return document;
    }

    private static void trustAllCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(
                    X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    X509Certificate[] certs, String authType) {
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
}
