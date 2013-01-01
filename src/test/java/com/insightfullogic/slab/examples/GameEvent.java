package com.insightfullogic.slab.examples;

import com.insightfullogic.slab.Cursor;

public interface GameEvent extends Cursor {

    public int getId();

    public void setId(int value);

    public long getStrength();

    public void setStrength(long value);

    public long getTarget();
    
    public void setTarget(long value);

}