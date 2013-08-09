package elr.minecraft.modded;

import elr.minecraft.versions.Library;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to abstract all Minecraft Forge versions.
 * @author Infernage
 */
public class RemoteMinecraftForge {
    private static class ForgeLibrary extends Library{
        private ForgeLibrary (String libName, String libURL){
            name = libName;
            url = libURL;
        }
    }
    private final static Library WRAPPER = new ForgeLibrary("net.minecraft:launchwrapper:1.3", null);
    private final static Library SCALA_LIBRARY = new ForgeLibrary("org.scala-lang:scala-library:2.10.2",
            "http://repo.maven.apache.org/maven2/");
    private final static Library SCALA_COMPILER = new ForgeLibrary("org.scala-lang:scala-compiler:2.10.2",
            "http://repo.maven.apache.org/maven2/");
    private final static Library ASM = new ForgeLibrary("org.ow2.asm:asm-all:4.1", null);
    private final static Library LZMA = new ForgeLibrary("lzma:lzma:0.0.1", null);
    public static List<Library> getForgeLibraries(){
        List<Library> result = new ArrayList<>();
        result.add(ASM);
        result.add(WRAPPER);
        result.add(LZMA);
        result.add(SCALA_COMPILER);
        result.add(SCALA_LIBRARY);
        return result;
    }
    
    private String version;
    private String mc_version_for;
    private String releaseDate;
    private String url;
    
    public RemoteMinecraftForge(String ver, String mc, String date, String url){
        version = ver;
        mc_version_for = mc;
        releaseDate = date;
        this.url = url;
    }
    
    public String getForgeVersion(){ return version; }
    public String getMCVersion(){ return mc_version_for; }
    public String getReleaseDate(){ return releaseDate; }
    public String getDownloadURL(){ return url; }
}
