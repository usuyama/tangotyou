package models;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import play.Logger;
import play.data.format.Formats;
import play.db.ebean.*;
import utils.InvalidUrlException;

import javax.persistence.*;

@Entity
public class Query extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    public Long user_id;
    public String word;
    public String url;
    public String site = null;
    @Formats.DateTime(pattern = "yyyy-MM-ddThh:mm:ss")
    public Date created_at;

    static Pattern weblioPattern = Pattern.compile("http://ejje.weblio.jp/content/(.+)");
    static Pattern eijiroPattern = Pattern.compile("http://eow.alc.co.jp/([^/]+)/");

    static String weblioRoot = "ejje.weblio.jp";
    static String eijiroRoot = "eow.alc.co.jp";

   public Query(User user, String url) throws InvalidUrlException {
       this.user_id = user.id;
       try {
           this.url = URLDecoder.decode(url, "UTF-8");
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }
       String word = null;
       String site = null;
       Logger.debug(this.url);
       String[] parts = this.url.split("/");
       if (parts[2].equals(weblioRoot)) {
           word = parts[4];
           site = "weblio";
       } else if (parts[2].equals(eijiroRoot)) {
           site = "英辞郎";
           if (this.url.contains("search?q=")) {
               try {
                   List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                   for(NameValuePair x: params) {
                        if (x.getName().equals("q")) {
                            word = x.getValue();
                            break;
                        }
                   }
                   if (word == null)
                       throw new InvalidUrlException("There is no q param in the url queries.");
               } catch (URISyntaxException e) {
                   throw new InvalidUrlException("We could not recognize the given url of eijiro.");
               }
           } else {
               word = parts[3];
           }
       }
       if (site != null) {
           this.word = word;
           this.site = site;
       } else {
           throw new InvalidUrlException("We could not recognize the given url.");
       }
   }

    public static List<Query> getAllQueriesByUser(User user) {
        List<Query> queries = find.where().eq("user_id", user.id).orderBy("created_at desc").findList();
        return queries;
    }

    public static Finder<Long, Query> find = new Finder(
            Long.class, Query.class
    );

    public static List<Query> all() {
        return find.all();
    }

    public static Query findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }

}
