/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host.messages;

import gems.charon.host.data.BundleID;
import com.sun.spot.util.IEEEAddress;
import com.sun.squawk.util.Arrays;
import gems.charon.host.exceptions.MessageConversionException;
import gems.charon.host.utils.Config;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Datagram;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Bundle implements Message, Datagram {

    BundleID id;
    long timestamp;
    private byte serviceClass;
    private byte stream;
    private byte[] payload;
    public static final int MAX_LENGTH = 1200;
    private int payloadIndex;
    private int endOfDataIndex;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Bundle() {
        reset();
        payload = new byte[MAX_LENGTH];
        dis = new DataInputStream(new BundleInputStream());
        dos = new DataOutputStream(new BundleOutputStream());
    }

    public Bundle(BundleID id) {
        this();
        this.id = id;
    }

    public Bundle(long address, int sequenceNumber) {
        this(new BundleID(address, sequenceNumber));
    }

    public byte getMessageType() {
        return 0x2;
    }

    public void setID(BundleID id) {
        this.id = id;
    }

    public BundleID getID() {
        return this.id;
    }

    public String getAddress() {
        return new IEEEAddress(id.getAddress()).asDottedHex();
    }

    public long getAddressAsLong() {
        return id.getAddress();
    }

    public byte[] getData() {
        byte[] buffer = new byte[getLength()];
        System.arraycopy(payload, 0, buffer, 0, buffer.length);
        return buffer;
    }

    public int getLength() {
        return endOfDataIndex;
    }

    /**
     * Not implemented
     */
    public int getOffset() {
        return 0;
    }

    /**
     * Not implemented
     */
    public void setAddress(String addr) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Not implemented
     */
    public void setAddress(Datagram reference) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Not implemented
     */
    public void setLength(int len) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Not implemented
     */
    public void setData(byte[] buffer, int offset, int len) {
        payload = Arrays.copy(buffer, offset, MAX_LENGTH, 0, len);
        payloadIndex = 0;
        endOfDataIndex = len;
    }

    /**
     * Ensures that the next read or write operation will read/write from the start of the radiogram
     */
    public void reset() {
        payloadIndex = endOfDataIndex = 0;
    }

    /**
     * Ensures that the next read operation will read from the start of the radiogram
     * @throws java.io.IOException unable to reset read pointer to beginning of datagram
     */
    public void resetRead() throws IOException {
        dis.reset();
    }

    /* (non-Javadoc)
     * @see java.io.DataInputStream#available()
     */
    public int available() throws IOException {
        return dis.available();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readFully(byte[])
     */
    public void readFully(byte[] b) throws IOException {
        dis.readFully(b);
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readFully(byte[], int, int)
     */
    public void readFully(byte[] b, int off, int len) throws IOException {
        dis.readFully(b, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#skipBytes(int)
     */
    public int skipBytes(int n) throws IOException {
        return dis.skipBytes(n);
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readBoolean()
     */
    public boolean readBoolean() throws IOException {
        return dis.readBoolean();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readByte()
     */
    public byte readByte() throws IOException {
        return dis.readByte();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readUnsignedByte()
     */
    public int readUnsignedByte() throws IOException {
        return dis.readUnsignedByte();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readShort()
     */
    public short readShort() throws IOException {
        return dis.readShort();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readUnsignedShort()
     */
    public int readUnsignedShort() throws IOException {
        return dis.readUnsignedShort();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readChar()
     */
    public char readChar() throws IOException {
        return dis.readChar();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readInt()
     */
    public int readInt() throws IOException {
        return dis.readInt();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readLong()
     */
    public long readLong() throws IOException {
        return dis.readLong();
    }

    /* (non-Javadoc)
     * @see java.io.DataInput#readUTF()
     */
    public String readUTF() throws IOException {
        return dis.readUTF();
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#write(int)
     */
    public void write(int b) throws IOException {
        dos.write(b);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        dos.write(b);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        dos.write(b, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeBoolean(boolean)
     */
    public void writeBoolean(boolean v) throws IOException {
        dos.writeBoolean(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeByte(int)
     */
    public void writeByte(int v) throws IOException {
        dos.writeByte(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeShort(int)
     */
    public void writeShort(int v) throws IOException {
        dos.writeShort(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeChar(int)
     */
    public void writeChar(int v) throws IOException {
        dos.writeChar(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeInt(int)
     */
    public void writeInt(int v) throws IOException {
        dos.writeInt(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeLong(long)
     */
    public void writeLong(long v) throws IOException {
        dos.writeLong(v);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeChars(java.lang.String)
     */
    public void writeChars(String s) throws IOException {
        dos.writeChars(s);
    }

    /* (non-Javadoc)
     * @see java.io.DataOutput#writeUTF(java.lang.String)
     */
    public void writeUTF(String str) throws IOException {
        dos.writeUTF(str);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double readDouble() throws IOException {
        return dis.readDouble();
    }

    public float readFloat() throws IOException {
        return dis.readFloat();
    }

    public void writeDouble(double v) throws IOException {
        dos.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        dos.writeFloat(v);
    }

    public byte getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(byte serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setZombie() {
        this.serviceClass = 0;
    }

    public byte getStream() {
        return stream;
    }

    public void setStream(byte stream) {
        this.stream = stream;
    }

    public String readLine() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void writeBytes(String s) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class BundleInputStream extends InputStream {

        public int read() {
            if (payloadIndex >= endOfDataIndex) {
                return -1;
            } else {
                return payload[payloadIndex++] & 0xFF;
            }
        }

        public void close() throws IOException {
            payload = null;
            super.close();
        }

        public int available() {
            return endOfDataIndex - payloadIndex;
        }

        public void reset() {
            payloadIndex = 0;
        }
    }

    private class BundleOutputStream extends OutputStream {

        public void write(int b) {
            if (endOfDataIndex >= payload.length) {
                throw new IndexOutOfBoundsException("Bundle is full");
            }
            payload[endOfDataIndex++] = (byte) b;
        }

        public void close() throws IOException {
            payload = null;
            super.close();
        }
    }

    public String toString() {
        if (id == null) {
            return "Bundle: no ID";
        }
        return id.toString() + "; TS: " + timestamp;
    }

    public byte[] marshal() throws MessageConversionException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeLong(id.getAddress());
            dout.writeInt(id.getSequenceNumber());
            dout.writeByte(serviceClass);
            dout.writeByte(stream);
            dout.writeLong(timestamp);
            dout.write(getData());
            dout.flush();
            return bout.toByteArray();
        } catch (IOException ex) {
            throw new MessageConversionException(ex.getMessage());
        }
    }

    public void unmarshal(byte[] data) throws MessageConversionException {
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(bin);
            reset();
            long address = din.readLong();
            int seqNum = din.readInt();
            id = new BundleID(address, seqNum);
            serviceClass = din.readByte();
            stream = din.readByte();
            timestamp = din.readLong();
            endOfDataIndex = din.read(payload);
            if(endOfDataIndex < 0) {
                endOfDataIndex = 0;
            }
        } catch (IOException ex) {
            throw new MessageConversionException(ex.getMessage());
        }
    }
}
