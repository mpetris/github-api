package org.kohsuke.github;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Gist
 *
 * @author Kohsuke Kawaguchi
 * @see GHUser#listGists() GHUser#listGists()
 * @see GitHub#getGist(String) GitHub#getGist(String)
 * @see GitHub#createGist() GitHub#createGist()
 * @see <a href="https://developer.github.com/v3/gists/">documentation</a>
 */
public class GHGist extends GHObject {

    final GHUser owner;
    final GitHub root;

    private String forks_url, commits_url, id, git_pull_url, git_push_url, html_url;

    @JsonProperty("public")
    private boolean _public;

    private String description;

    private int comments;

    private String comments_url;

    private final Map<String, GHGistFile> files;

    @JsonCreator
    private GHGist(@JacksonInject GitHub root,
            @JsonProperty("owner") GHUser owner,
            @JsonProperty("files") Map<String, GHGistFile> files) {
        this.root = root;
        for (Entry<String, GHGistFile> e : files.entrySet()) {
            e.getValue().fileName = e.getKey();
        }
        this.files = Collections.unmodifiableMap(files);
        this.owner = root.getUser(owner);
    }

    /**
     * Gets owner.
     *
     * @return User that owns this Gist.
     * @throws IOException
     *             the io exception
     */
    public GHUser getOwner() throws IOException {
        return owner;
    }

    /**
     * Gets forks url.
     *
     * @return the forks url
     */
    public String getForksUrl() {
        return forks_url;
    }

    /**
     * Gets commits url.
     *
     * @return the commits url
     */
    public String getCommitsUrl() {
        return commits_url;
    }

    /**
     * Gets git pull url.
     *
     * @return URL like https://gist.github.com/gists/12345.git
     */
    public String getGitPullUrl() {
        return git_pull_url;
    }

    /**
     * Gets git push url.
     *
     * @return the git push url
     */
    public String getGitPushUrl() {
        return git_push_url;
    }

    public URL getHtmlUrl() {
        return GitHubClient.parseURL(html_url);
    }

    /**
     * Is public boolean.
     *
     * @return the boolean
     */
    public boolean isPublic() {
        return _public;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets comment count.
     *
     * @return the comment count
     */
    public int getCommentCount() {
        return comments;
    }

    /**
     * Gets comments url.
     *
     * @return API URL of listing comments.
     */
    public String getCommentsUrl() {
        return comments_url;
    }

    /**
     * Gets file.
     *
     * @param name
     *            the name
     * @return the file
     */
    public GHGistFile getFile(String name) {
        return files.get(name);
    }

    /**
     * Gets files.
     *
     * @return the files
     */
    public Map<String, GHGistFile> getFiles() {
        return files;
    }

    String getApiTailUrl(String tail) {
        String result = "/gists/" + id;
        if (!StringUtils.isBlank(tail)) {
            result += StringUtils.prependIfMissing(tail, "/");
        }
        return result;
    }

    /**
     * Star.
     *
     * @throws IOException
     *             the io exception
     */
    public void star() throws IOException {
        root.createRequest().method("PUT").setRawUrlPath(getApiTailUrl("star")).send();
    }

    /**
     * Unstar.
     *
     * @throws IOException
     *             the io exception
     */
    public void unstar() throws IOException {
        root.createRequest().method("DELETE").setRawUrlPath(getApiTailUrl("star")).send();
    }

    /**
     * Is starred boolean.
     *
     * @return the boolean
     * @throws IOException
     *             the io exception
     */
    public boolean isStarred() throws IOException {
        return root.createRequest().withUrlPath(getApiTailUrl("star")).fetchHttpStatusCode() / 100 == 2;
    }

    /**
     * Forks this gist into your own.
     *
     * @return the gh gist
     * @throws IOException
     *             the io exception
     */
    public GHGist fork() throws IOException {
        return root.createRequest().method("POST").withUrlPath(getApiTailUrl("forks")).fetch(GHGist.class);
    }

    /**
     * List forks paged iterable.
     *
     * @return the paged iterable
     */
    public PagedIterable<GHGist> listForks() {
        return root.createRequest().withUrlPath(getApiTailUrl("forks")).toIterable(GHGist[].class, null);
    }

    /**
     * Deletes this gist.
     *
     * @throws IOException
     *             the io exception
     */
    public void delete() throws IOException {
        root.createRequest().method("DELETE").withUrlPath("/gists/" + id).send();
    }

    /**
     * Updates this gist via a builder.
     *
     * @return the gh gist updater
     * @throws IOException
     *             the io exception
     */
    public GHGistUpdater update() throws IOException {
        return new GHGistUpdater(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GHGist ghGist = (GHGist) o;
        return id.equals(ghGist.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
