package kinstalk.com.qloveaicore.txsdk;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.PatternMatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AssetCache {
    private static final int MAXDEPTH = 5;
    public static String mAssetStore = null;
    protected Context mContext;
    protected AssetManager mAssetManager;

    /**
     * Creates a new AssetCache to enable C-level access to application asset
     * files.
     * 
     * @param context
     *            application context for assets.
     * @param assetPrefix
     *            string prepended to file name lookups. Include trailing / if
     *            using a subdirectory.
     */
    public AssetCache(Context context, String assetPrefix) {
        mContext = context;
        mAssetStore = assetPrefix;
        mAssetManager = context.getAssets();
    }

    /**
     * Returns the absolute path to an asset file under assetPrefix
     * {@link #AssetCache(Context, String)}
     * 
     * @param tail
     *            last portion of the asset file, will be appended to the
     *            assetPrefix supplied in the constructor.
     * @return absolute path to file.
     * @throws IOException
     */
    public String getPath(String tail) throws IOException {
        final String path = mAssetStore + tail;
        // Check to see whether the file exists in local storage.
        final File check = new File(mContext.getFilesDir(), path);
        if (check.exists() && check.isFile() && check.lastModified() > packageInstallTime()) {
            // Log.i("assetFile", "file found: " + check.getAbsoluteFile());
            return check.getAbsolutePath();
        }
        // Not found or too old in local storage, check assets, copy to local
        // storage.
        final InputStream source = mAssetManager.open(path);
        // Make sure the target parent directory exists
        check.getParentFile().mkdirs();
        final FileOutputStream target = new FileOutputStream(check);
        copyFile(source, target);
        source.close();
        target.close();
        return check.getAbsolutePath();
    }

    /**
     * @param tail
     *            tail portion of the asset file, will be appended to the
     *            assetPrefix supplied in the constructor.
     * @return an InputStream handle that can read (stream only) from the named
     *         asset.
     * @throws IOException
     */
    public InputStream getStream(String tail) throws IOException {
        final String path = mAssetStore + tail;
        return mAssetManager.open(path);
    }

    public String[] glob(String pattern) throws IOException {
        final List<String> a = new ArrayList<String>();
        final String psimple = pattern.replaceAll("\\*", "\\.\\*");
        final int depth = pattern.replaceAll("[^" + File.separator + "]", "").length();
        final PatternMatcher p = new PatternMatcher(psimple, PatternMatcher.PATTERN_SIMPLE_GLOB);
        for (final String path : list("", depth)) {
            if (p.match(path)) {
                a.add(path);
            }
        }
        return a.toArray(new String[0]);
    }

    public String[] list(String tail) throws IOException {
        return list(tail, MAXDEPTH);
    }

    public String[] list(String tail, int depth) throws IOException {
        final List<String> a = new ArrayList<String>();
        final int start = mAssetStore.length();
        for (final String path : listrec(mAssetStore + tail, depth)) {
            a.add(path.substring(start));
        }
        return a.toArray(new String[0]);
    }

    private void copyFile(InputStream source, OutputStream target) throws IOException {
        final byte[] buffer = new byte[2048];
        int read;
        while ((read = source.read(buffer)) >= 0) {
            target.write(buffer, 0, read);
        }
    }

    private List<String> listrec(String tail, int depth) throws IOException {
        final List<String> a = new ArrayList<String>();
        final String name = stripTrailingSeparator(tail);
        final String[] paths = mAssetManager.list(name);
        if (paths.length == 0) {
            a.add(name);
        } else {
            for (final String path : paths) {
                if (depth > 0) {
                    a.addAll(listrec(name + File.separator + path, depth - 1));
                } else {
                    a.add(name + File.separator + path);
                }
            }
        }
        return a;
    }

    private long packageInstallTime() {
        try {
            final PackageManager pm = mContext.getPackageManager();
            final ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(), 0);
            return new File(ai.sourceDir).lastModified();
        } catch (final NameNotFoundException e) {
            // This really shouldn't ever happen.
            e.printStackTrace();
            throw new RuntimeException("Could not determine this package name");
        }
    }

    private String stripTrailingSeparator(String path) {
        if (path.endsWith(File.separator)) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

}