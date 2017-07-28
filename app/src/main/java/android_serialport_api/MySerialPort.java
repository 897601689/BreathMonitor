package android_serialport_api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/1.
 */

public class MySerialPort {

    SerialPort com;
    FileOutputStream mOutputStream;
    FileInputStream mInputStream;

    public MySerialPort() {

    }

    public boolean Open(String path, int baudrate) {
        try {
            com = new SerialPort(new File(path), baudrate, 0);
            mInputStream = (FileInputStream) com.getInputStream();
            mOutputStream = (FileOutputStream) com.getOutputStream();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean Close() {
        if (com != null) {
            com.close();
            com = null;
            return true;
        } else {
            return false;
        }
    }

    public byte[] Read() throws IOException {
        if (mInputStream == null) throw new IOException();
        int len;
        try {
            len = mInputStream.available();
            if (len > 0) {
                byte[] buffer = new byte[len];
                len = mInputStream.read(buffer);

                //String ss = String.format("%X", buffer[0]);
                //System.out.println(ss);
                return buffer;
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw e;
        }
        return null;
    }

    public void Write(String data) throws IOException {
        if (data.equals("")) return;
        String[] array = data.split(" ");
        byte[] buffer = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            int exterData = Integer.valueOf(array[i], 16);
            buffer[i] = (byte) exterData;
            //String ss = String.valueOf(buffer[i]);
            //Log.i("read", ss);
        }
        mOutputStream.write(buffer);
    }
    public void Write(byte[] buffer) throws IOException {
        mOutputStream.write(buffer);
    }
}
