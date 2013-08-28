package elr.externalmodules.modpackmodule.engine.uploader.mega;

/**
 * Object sent to the server. Used to upload a file to a Mega.co.nz
 * @author Infernage
 */
class MegaObject {
    private String email;
    private String password;
    private String filename;
    private byte[] file;
    private long filesize;
    
    private MegaObject(String em, String pass, String name, byte[] data, long size){
        email = em;
        password = pass;
        filename = name;
        file = data;
        filesize = size;
    }
}
