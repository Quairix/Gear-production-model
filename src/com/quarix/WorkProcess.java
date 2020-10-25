package com.quarix;

import java.util.ArrayList;
import java.util.List;

class WorkProcess {
    private double time;
    private List<Gear> lineConcreting;
    private List<Gear> lineHarding;
    private List<Gear> Fsort;
    private List<Gear> Ssort;

    WorkProcess() {
        Concrete.isConcreting = false;
        Hard.isHard = false;
        time = 0;
        lineConcreting = new ArrayList<>();
        lineHarding = new ArrayList<>();
        Fsort = new ArrayList<>();
        Ssort = new ArrayList<>();
    }

    int getFsortSize() {
        return Fsort.size();
    }

    void addFsort(Gear gear) {
        Fsort.add(gear);
    }

    int getSsortSize() {
        return Ssort.size();
    }

    void addSsort(Gear gear) {
        Ssort.add(gear);
    }

    void startHarding(double time) {
        Hard.isHard = true;
        Hard.timeOut = time + this.time;
    }

    void startConcreting(double time) {
        Concrete.isConcreting = true;
        Concrete.timeOut = time + this.time;
    }

    void endHarding() {
        Hard.isHard = false;
        Hard.timeOut = 0;
    }

    void endConcreting() {
        Concrete.isConcreting = false;
        Concrete.timeOut = 0;
    }

    double getTime() {
        return time;
    }

    void addTime(int time) {
        this.time += time;
    }

    void addGearConcrecing() {
        lineConcreting.add(new Gear());
    }

    void addGearHarding(Gear gear) {
        lineHarding.add(gear);
    }

    Gear removeGearConcrecing(double time) {
        Gear tmp = lineConcreting.get(0);
        tmp.setTime(time);
        tmp.setConcrete(true);
        if (lineConcreting.size() > 0)
            lineConcreting.remove(lineConcreting.get(0));
        return tmp;
    }

    Gear removeGearHarding(double time) {
        Gear tmp = lineHarding.get(0);
        tmp.setTime(tmp.getTime() + time);
        tmp.setHard(true);
        if (lineHarding.size() > 0)
            lineHarding.remove(lineHarding.get(0));
        return tmp;
    }

    boolean isLineConcreting() {
        return lineConcreting.size() > 0;
    }

    boolean isLineHarding() {
        return lineHarding.size() > 0;
    }

    int getHardingLineSize() {
        return lineHarding.size();
    }

    int getConcretingLineSize() {
        return lineConcreting.size();
    }
}
