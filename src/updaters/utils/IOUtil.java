/* Generated by Together 
 *
 *  * Integerated autoupdater into MekWarsAutoupdate --Torren 
 * 
 */

package updaters.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class IOUtil
{

    public static void copy(Reader in, Writer out) throws IOException
    {
        char[] buffer = new char[BUFFER_SIZE];
        int bytesRead;
        while( (bytesRead=in.read(buffer)) != -1 )
        {
            out.write(buffer,0,bytesRead);
        }
    }

    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while( (bytesRead=in.read(buffer)) != -1 )
        {
            out.write(buffer,0,bytesRead);
        }
    }

    public static void copy(String inputFileName, String outputFileName)
        throws IOException
    {
        InputStream in = new FileInputStream(inputFileName);
        OutputStream out = new FileOutputStream(outputFileName);
        copy(in, out);
        in.close();
        out.close();
    }

    public static String[] getLines(String inputFileName) throws IOException {

    	BufferedReader in = new BufferedReader(new FileReader(inputFileName));
    	List<String> lines = new ArrayList<String>();
    	String line;
    	while((line = in.readLine()) != null ) {
    		lines.add(line);
    	}

    	Iterator<String> iter = lines.iterator();
    	int i = 0;
    	String[] retval = new String[lines.size()];
    	while(iter.hasNext()) {
    		retval[i++] = iter.next();
    	}
    	in.close();

    	return retval;
    }

    /**
     * Gets the CRC32 value from the given InputStream.  Note that the input
     * stream should be pointing at the beginning when it is passed in
     * (i.e. nothing should have been read from it yet) and it will be
     * closed after the call completes.
     * @param is InputStream for which to calculate the checksum.
     * @return the checksum of is
     */
    public static long getCRC32(InputStream is) throws IOException {
        
    	CRC32 crc = new CRC32();
        CheckedInputStream cis = new CheckedInputStream(is, crc);
        
        while(cis.read(THROWAWAY_BUFFER,0,THROWAWAY_BUFFER.length) != -1) {
        	//useful function here is cis.read().
        }

        cis.close();
        return crc.getValue();
    }


    public static String trimLeadingFileSeparator(String url)
    {
        while( url.endsWith("/") || url.endsWith("\\")
               || url.endsWith(File.separator) )
        {
            url = url.substring(0,url.length()-1);
        }

        return url;
    }

    public static String trimTrailingFileSeparator(String url)
    {
        while( url.startsWith("/") || url.startsWith("\\")
               || url.startsWith(File.separator) )
        {
            url = url.substring(1);
        }

        return url;
    }

    public static String trimFileSeparator(String url)
    {
        url = trimLeadingFileSeparator(url);
        url = trimTrailingFileSeparator(url);

        return url;
    }

    /**
     * Deletes deep and all of its parent directories up to base.
     * Behavior is undefined if base is not a parent of deep ; )
     */
    public static void removeDirectoriesBetween(File base, File deep)
    {
        while( deep.getAbsolutePath().startsWith(base.getAbsolutePath()) &&
               !deep.equals(base) )
        {
            deep.delete();
            deep = new File(deep.getParent());
        }
    }

    /**
     * Return all of the files and directories inside of dir.
     */
    public static List<File> getTree(File dir)
    {
    	List<File> list = new ArrayList<File>();
    	getTree(dir, list);
    	return list;
    }

    /**
     * Add all of the files and directories inside of dir to list.
     * Normally you just want to use getTree(File) instead.
     */
    public static void getTree(File dir, List<File> list)
    {
    	list.add(dir);
    	File[] fileList = dir.listFiles();
    	if( fileList != null )
    	{
    	    for( int i = 0; i < fileList.length; ++i )
    	    {
    		getTree(fileList[i], list);
    	    }
    	}
    }

    /**
     * Breaks the jar file name away from the embedded file in an URL
     * or filename.  The individual parts (without the '!') come back
     * in a String array.
     * Example:
     * parseJarURL("xithdemo/cosm.jar!/com/navtools/util/IOUtil.class")
     * yields new String[] { "xithdemo/cosm.jar",
     *                       "/com/navtools/util/IOUtil.class"}
     * @param url the url or filename to parse
     * @return the list of parts
     **/
    public static String[] parseJarURL(String url)
    {
        List<String> parts = new ArrayList<String>();
        StringTokenizer toker = new StringTokenizer(url,"!");
        while(toker.hasMoreTokens())
        {
            parts.add(toker.nextToken());
        }

        String[] retval = new String[parts.size()];
        for(int i = 0; i < retval.length; ++i)
        {
            retval[i] = parts.get(i);
        }

        return retval;
    }

    public static String fixJarURL(String jarURL)
    {
        if( jarURL.indexOf("!") != -1 &&
            !jarURL.startsWith("jar:") )
        {
            jarURL = "jar:" + jarURL;
        }

        return jarURL;
    }

    /**
     * @param url - the url to be delimited
     * @return string with all spaces converted to %20
     */
    public static String delimitURL(String url)
    {
        return url.replaceAll(" ", "%20");
    }

    /**
     * @param path - path that came from another box and may not be correct
     *               for this OS.
     * @return string with fixed path
     */
    public static String fixPath(String path)
    {
        String newPath = removeLeadingDotSlash(path);
        
    	if (File.separator.equals("\\"))
        {
    	    newPath = newPath.replaceAll("/","\\");
        }
    	else if (File.separator.equals("/"))
    	{
    	    newPath = newPath.replaceAll("\\","/");
        }
    
    	return newPath;
    }

    /**
     * @param path - path to be modified
     * @return path without any leading ./
     */
    public static String removeLeadingDotSlash(String path)
    {
        /*String newPath = path.replaceAll("\\\\","\\");
        newPath = newPath.replaceAll("//","/");*/
        String newPath = IOUtil.replaceString(path,".\\","");
        return IOUtil.replaceString(newPath,"./","");
    }

    public static void main(String[] args) throws Exception
    {
        copy(new BufferedInputStream(new URL(args[0]).openStream()), new FileOutputStream(args[1]));
    }

    /**
     * @param source - the original string
     * @param old - the old value to replace with newstring
     * @param newstring - the new value to use for replacing old string
     * @return string where all occurrences of old in source are replaced with newstring
     */
    public static String replaceString( String source, String old, String newstring )
    {
        String sRet = "";

        if ( source != null && old != null && newstring != null )
        {
            for ( int i = 0; i < source.length(); i++ )
            {
                //we need to make sure that we do not take a sub
                //string that is longer than our original word
                if ( ( source.length() - i ) < ( old.length() ) )
                {
                    while ( i < source.length() )
                    {
                        sRet += source.substring( i, i + 1 );
                        i++;
                    }
                    break;
                }

                if ( source.substring( i, i + old.length() ).equals( old ) )
                {
                    sRet += newstring;
                    i += old.length() - 1;
                }
                else
                {
                    sRet += source.substring( i, i + 1 );
                }
            }
        }

        return sRet;
    }

    public static final int BUFFER_SIZE = 8192;
    public static final byte[] THROWAWAY_BUFFER = new byte[BUFFER_SIZE];
}
