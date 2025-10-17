package org.example;


public class LukaNumber {
    protected short index = -1;
    protected long num = -1;

    public LukaNumber(short index){
        this.index = index;
        this.num = this.CalcLukaNum();
    }

    private int CalcLukaNum() {
        int prev = 2, num = 1;

        if(index == 0 ) return prev;
        if(index == 1) return num;

        for(int i = 2; i <= index; i++){
            int next = prev + num;
            prev = num;
            num = next;
        }

        return num;
    }

    public long getNum() {
        return this.num;
    }
    public short getIndex() {return this.index;}
}