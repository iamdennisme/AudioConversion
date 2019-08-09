/*
 * Copyright 2008 CoreMedia AG, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hbbclub.m4aencoder.aacenc.coremedia.iso.boxes.sampleentry;


import com.hbbclub.m4aencoder.aacenc.coremedia.iso.BoxParser;
import com.hbbclub.m4aencoder.aacenc.coremedia.iso.IsoTypeReader;
import com.hbbclub.m4aencoder.aacenc.coremedia.iso.IsoTypeWriter;
import com.hbbclub.m4aencoder.aacenc.coremedia.iso.boxes.Box;
import com.hbbclub.m4aencoder.aacenc.coremedia.iso.boxes.ContainerBox;
import com.hbbclub.m4aencoder.aacenc.googlecode.mp4parser.AbstractBox;
import com.hbbclub.m4aencoder.aacenc.googlecode.mp4parser.util.ByteBufferByteChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base class for all sample entries.
 */
public abstract class SampleEntry extends AbstractBox implements ContainerBox {


    protected List<Box> boxes = new LinkedList<>();
    private int dataReferenceIndex;
    private BoxParser boxParser;


    protected SampleEntry(String type) {
        super(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDataReferenceIndex() {
        return dataReferenceIndex;
    }

    public void setDataReferenceIndex(int dataReferenceIndex) {
        this.dataReferenceIndex = dataReferenceIndex;
    }

    public void addBox(AbstractBox b) {
        boxes.add(b);
    }

    public boolean removeBox(Box b) {
        return boxes.remove(b);
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = new LinkedList<Box>(boxes);
    }

    @SuppressWarnings("unchecked")
    public <T extends Box> List<T> getBoxes(Class<T> clazz, boolean recursive) {
        List<T> boxesToBeReturned = new ArrayList<T>(2);
        for (Box boxe : boxes) { //clazz.isInstance(boxe) / clazz == boxe.getClass()?
            if (clazz == boxe.getClass()) {
                boxesToBeReturned.add((T) boxe);
            }

            if (recursive && boxe instanceof ContainerBox) {
                boxesToBeReturned.addAll(((ContainerBox) boxe).getBoxes(clazz, recursive));
            }
        }
        // Optimize here! Spare object creation work on arrays directly! System.arrayCopy
        return boxesToBeReturned;
        //return (T[]) boxesToBeReturned.toArray();
    }

    @SuppressWarnings("unchecked")
    public <T extends Box> List<T> getBoxes(Class<T> clazz) {
        return getBoxes(clazz, false);
    }

    @Override
    public void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        super.parse(readableByteChannel, header, contentSize, boxParser);
        this.boxParser = boxParser;
    }


    public void _parseReservedAndDataReferenceIndex(ByteBuffer content) {
        content.get(new byte[6]); // ignore 6 reserved bytes;
        dataReferenceIndex = IsoTypeReader.readUInt16(content);
    }

    public void _parseChildBoxes(ByteBuffer content) {
        while (content.remaining() > 8) {
            try {
                boxes.add(boxParser.parseBox(new ByteBufferByteChannel(content), this));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        setDeadBytes(content.slice());
    }

    public void _writeReservedAndDataReferenceIndex(ByteBuffer bb) {
        bb.put(new byte[6]);
        IsoTypeWriter.writeUInt16(bb, dataReferenceIndex);
    }

    public void _writeChildBoxes(ByteBuffer bb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel(baos);
        try {
            for (Box box : boxes) {
                box.getBox(wbc);
            }
            wbc.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot happen. Everything should be in memory and therefore no exceptions.");
        }
        bb.put(baos.toByteArray());
    }

    public long getNumOfBytesToFirstChild() {
        long sizeOfChildren = 0;
        for (Box box : boxes) {
            sizeOfChildren += box.getSize();
        }
        return getSize() - sizeOfChildren;
    }

}
