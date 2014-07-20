package mobnav.canvas;


/**
 *
 * @author 2cool
 */
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;

public class BufferedRead {
   private InputStream is;
    final byte[]buf=new byte[64];
    private int i, av;
    private long readed=0;
public long readed(){return readed;}
 public long fileSize(){
     if (fileSize<0)
         throw new RuntimeException("no such method, use another constructor");
    return fileSize;
 }
 private long fileSize=-1;
 public BufferedRead(InputStream is){
      this.is=is;
      loadBuf();
  }
 FileConnection fc=null;
 public BufferedRead(final String fn)throws IOException{
    is=null;
    fc = (FileConnection) Connector.open(fn);
    fileSize=fc.fileSize();
    is=fc.openInputStream();
    loadBuf();
 }
  public void close(){
      try{
        if (is!=null)
            is.close();
      }catch(Exception ex){}
      try{
        if (fc!=null)
            fc.close();
      }catch(Exception ex){}
  }
  private  int loadBuf(){
        i=0;
        try{
            return av=is.read(buf);
        }catch (IOException ex) {System.out.println("ERROR");return av=0;}
    }
 public   int read(){
        if (i>=av && loadBuf()<=0)
                return -1;
       return 0xff&buf[i++];
}
public String readString(){
     String strr="";
     int ch;
     //    System.out.println((int)'\n');
     while ((ch=read())!=0xd && ch!=0xa && ch!=-1){
    	 readed++;
         //   if (ch>=20)
           strr+=((char)ch);
    }
     if (ch==0xd)
    	 read();
     if (strr.length()==0)
         return null;
     else
        return strr;
     }
public String readUTF_8String(){
        try {
            String str=readString();
            if (str!=null)
                return new String(str.getBytes(),"UTF-8");
            else 
                return null;
        } catch (UnsupportedEncodingException ex) {}
        return "abrakadabra";
}

public int readInteger(){
	readed+=4;
    if (av-i>3)
        return (0xff&buf[i++])|((0xff&buf[i++])<<8)|((0xff&buf[i++])<<16)|((0xff&buf[i++])<<24);
    else
        return read()|(read()<<8)|(read()<<16)|(read()<<24);
}
public float readFloat(){
	readed+=4;
    int ib=0;
    if (av-i>3){
        int mask=0xff;
        ib=  buf[i++]&mask;
        ib|=(buf[i++]&mask)<<8;
        ib|=(buf[i++]&mask)<<16;
        ib|=(buf[i++]&mask)<<24;
    }else{
        ib=  read();
        ib|=(read())<<8;
        ib|=(read())<<16;
        ib|=(read())<<24;
    }
    return Float.intBitsToFloat(ib);
}
public double readDouble(){
	readed+=8;
    long l=0;
    if (av-i>7){
        long mask=0xff;
        l=  (long)buf[i++]&mask;
        l|=((long)buf[i++]&mask)<<8;
        l|=((long)buf[i++]&mask)<<16;
        l|=((long)buf[i++]&mask)<<24;
        l|=((long)buf[i++]&mask)<<32;
        l|=((long)buf[i++]&mask)<<40;
        l|=((long)buf[i++]&mask)<<48;
        l|=((long)buf[i++]&mask)<<56;
    }else{
        l=  (long)read();
        l|=((long)read())<<8;
        l|=((long)read())<<16;
        l|=((long)read())<<24;
        l|=((long)read())<<32;
        l|=((long)read())<<40;
        l|=((long)read())<<48;
        l|=((long)read())<<56;
    }
    return Double.longBitsToDouble(l);
}

}
