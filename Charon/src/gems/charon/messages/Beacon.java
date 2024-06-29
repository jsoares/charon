/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.messages;

import gems.charon.exceptions.MessageConversionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Beacon implements Message {

    long time = -1;
    int age = 0;
    short capacity;
    byte power;
    long edd;
    boolean baseStation = false;

    public Beacon() {
    }

    public byte getMessageType() {
        return 0x1;
    }

    public long getEDD() {
        return edd;
    }

    public void setEDD(long edd) {
        this.edd = edd;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isBaseStation() {
        return baseStation;
    }

    public void setBaseStation() {
        baseStation = true;
        capacity = Short.MAX_VALUE;
        edd = 0;
        age = 0;
    }

    public short getCapacity() {
        return capacity;
    }

    public void setCapacity(short capacity) {
        this.capacity = capacity;
    }

    public byte getPower() {
        return power;
    }

    public void setPower(byte power) {
        this.power = power;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public byte[] marshal() throws MessageConversionException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeLong(time);
            dout.writeInt(age);
            dout.writeShort(capacity);
            dout.writeByte(power);
            dout.writeLong(edd);
            dout.writeBoolean(baseStation);
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
            time = din.readLong();
            age = din.readInt();
            capacity = din.readShort();
            power = din.readByte();
            edd = din.readLong();
            baseStation = din.readBoolean();
        } catch (IOException ex) {
            throw new MessageConversionException(ex.getMessage());
        }
    }

}
