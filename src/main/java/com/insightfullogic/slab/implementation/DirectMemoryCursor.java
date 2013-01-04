package com.insightfullogic.slab.implementation;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

import sun.misc.Unsafe;

import com.insightfullogic.slab.Cursor;
import com.insightfullogic.slab.InvalidSizeException;

@SuppressWarnings("restriction")
public abstract class DirectMemoryCursor implements Cursor {
	
	static final String INTERNAL_NAME = Type.getInternalName(DirectMemoryCursor.class);
	
	protected static final Unsafe unsafe;

	static {
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			unsafe = (Unsafe) singleoneInstanceField.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private final int sizeInBytes;
	private final AllocationHandler handler;
	protected final long startAddress;

	private int index;
	protected long pointer;

	public DirectMemoryCursor(int count, int sizeInBytes, AllocationHandler handler) {
		this.sizeInBytes = sizeInBytes;
		this.handler = handler;
		startAddress = unsafe.allocateMemory(sizeInBytes * count);

		move(0);
	}

	public void close() {
		handler.free();
		unsafe.freeMemory(startAddress);
	}

	public void move(int index) {
		this.index = index;
		pointer = startAddress + (sizeInBytes * index);
	}

	public int getIndex() {
		return index;
	}

	public void resize(int newSize) {
		if (newSize <= index)
			throw new InvalidSizeException("You can't resize a slab to below the index currently pointed at");

		int newSizeInBytes = sizeInBytes * newSize;
		handler.resize(newSize, newSizeInBytes);
		unsafe.reallocateMemory(startAddress, newSizeInBytes);
	}

}
