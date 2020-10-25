package com.quarix;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class lab2 {
    private JPanel panel;
    private JSpinner t1spinner;
    private JButton startButton;
    private JSpinner nspinner;
    private JTable table1;
    private JSpinner t2spinner;
    private JSpinner t3spinner;
    private JSpinner time1spinner;
    private JSpinner time2spinner;
    private JLabel ssLabel;
    private JLabel fsortLabel;
    private JLabel prozLabel;
    private JLabel outResultLabel;
    private JTable debugTable;
    private JProgressBar progressBar1;
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModelDebug;

    private lab2() {
        tableModel = new DefaultTableModel();
        Object[] columnsHeader = new String[]{"t0, мин", "Поступление", "ОчередьА", "Бетонирование", "ОчередьБ", "Закалка"};
        Object[] columnsHeaderDebug = new String[]{"T1", "T2", "prod", "price", "percent"};
        tableModel.setColumnIdentifiers(columnsHeader);
        tableModelDebug = new DefaultTableModel();
        tableModelDebug.setColumnIdentifiers(columnsHeaderDebug);
        debugTable.setModel(tableModelDebug);
        table1.setModel(tableModel);
        List<listItem> list = new ArrayList<>();
        time1spinner.setValue(15);
        time2spinner.setValue(10);
        nspinner.setValue(400);

        startButton.addActionListener(e -> {

            boolean change = false;
            int iteration = 0;
            int time1Max = (int) time1spinner.getValue(), time2Max = (int) time2spinner.getValue();
            int time1;
            int time2;
            while (tableModelDebug.getRowCount() > 0)
                tableModelDebug.removeRow(0);
            //while (iteration++ < 11) {
            for (time1 = time1Max; time1 <= time1Max + 10; time1++)
                for (time2 = time2Max; time2 <= time2Max + 10; time2++) {
                    time1spinner.setValue(time1);
                    time2spinner.setValue(time2);
                    while (tableModel.getRowCount() > 0)
                        tableModel.removeRow(0);

                    WorkProcess wp = new WorkProcess();
                    int countF = (int) nspinner.getValue();
                    Iteration it = new Iteration();
                    double t1 = it.process();
                    double t2;
                    double t3;
                    double sTimeCon = 0;
                    double tempTimeC = 0;
                    double sTimeHard = 0;
                    double tempTimeH = 0;
                    int count = 0;
                    boolean newGear = false;
                    /*
                     * lab2 + lab4
                     */
                    Random rnd = new Random();
                    int m2 = 10;
                    double sigma2 = 2.5;
                    double lambda = 0.03;

                    t2 = m2 + rnd.nextGaussian() * sigma2;
                    t3 = expGen(lambda);

                    double nextGearTime = 0;
                    tableModel.insertRow(tableModel.getRowCount(), new String[]{"0", "+", "0", "-", "0", "-"});
                    int iterator = 1;
                    Gear gear = new Gear();
                    while (count < countF || Concrete.isConcreting || Hard.isHard || wp.getConcretingLineSize() > 0 || wp.getHardingLineSize() > 0) {
                        wp.addTime(iterator);

                        if (wp.getTime() >= nextGearTime && count < countF) {//new gear?
                            newGear = true;
                            wp.addGearConcrecing();
                            count++;
                            nextGearTime += t1;
                            if (change)
                                t1 = it.process();
                        } else
                            newGear = false;
                        if (wp.isLineConcreting() && !Concrete.isConcreting) {
                            gear = wp.removeGearConcrecing(t2);
                            wp.startConcreting(t2);
                            tempTimeC = wp.getTime();
                        }

                        if (Concrete.timeOut <= wp.getTime() && Concrete.timeOut != 0) { //Remove from concreting and add to harding
                            wp.endConcreting();
                            wp.addGearHarding(gear);
                            sTimeCon += wp.getTime() - tempTimeC;

                            if (change)
                                t2 = m2 + rnd.nextGaussian() * sigma2;
                            //System.out.println(t2);
                        }

                        if (wp.isLineHarding() && !Hard.isHard) {
                            gear = wp.removeGearHarding(t3);
                            wp.startHarding(t3);
                            tempTimeH = wp.getTime();
                        }

                        if (Hard.timeOut <= wp.getTime() && Hard.timeOut != 0) { //Remove from concreting and add to harding
                            wp.endHarding();
                            sTimeHard += wp.getTime() - tempTimeH;
                            if (gear.getTime() < time2)
                                wp.addGearHarding(gear);
                            else if (gear.getTime() >= time1)
                                wp.addFsort(gear);
                            else wp.addSsort(gear);

                            if (change)
                                t3 = expGen(lambda);
                            //System.out.println( t3);
                        }
                        if (wp.getTime() % 2 == 0) {
                            String[] tableLineNew = {String.valueOf((int) wp.getTime()), newGear ? "+" : "-", String.valueOf(wp.getConcretingLineSize()), Concrete.isConcreting ? "+" : "-", String.valueOf(wp.getHardingLineSize()),
                                    Hard.isHard ? "+" : "-"};
                            tableModel.insertRow(tableModel.getRowCount(), tableLineNew);
                        }
                    }
                    outResultLabel.setText(wp.getFsortSize() + "/" + wp.getSsortSize());
                    double ss = (sTimeCon * 0.01 + sTimeHard * 0.2) / countF;
                    ssLabel.setText(String.format("%1$,.2f", ss) + "$");
                    double fsort = (wp.getFsortSize() * 100 / countF);
                    fsortLabel.setText(fsort + "%");
                    double proz = (countF * 60.0 / wp.getTime());
                    prozLabel.setText(String.format("%1$,.2f", proz) + "/ч");

                    String[] tableLineDebug = {String.valueOf(time1), String.valueOf(time2), String.format("%1$,.2f", proz), String.valueOf(ss), String.format("%1$,.2f", fsort / 100)};
                    tableModelDebug.insertRow(tableModelDebug.getRowCount(), tableLineDebug);
                    list.add(new listItem(time1, time2, proz, ss, fsort / 100));
                    progressBar1.setValue(iteration++);
                }

            String[] tableLineDebug = {"=", "=", "=", "=", "="};
            tableModelDebug.insertRow(tableModelDebug.getRowCount(), tableLineDebug);

            List<listItem> toRemove = new ArrayList<>();
            for (listItem f : list)
                for (listItem s : list) {
                    if (!f.equals(s) && (f.proz <= s.proz && f.price >= s.price && f.percent <= s.percent))
                        toRemove.add(f);
                }

            for(listItem r : toRemove)
                list.remove(r);

            for (listItem l : list) {
                String[] tableLineDebugRes = {String.valueOf(l.T1), String.valueOf(l.T2), String.format("%1$,.2f", l.proz), String.valueOf(l.price), String.format("%1$,.2f", l.percent)};
                tableModelDebug.insertRow(tableModelDebug.getRowCount(), tableLineDebugRes);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Завод");
        frame.setContentPane(new lab2().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);
    }

    public double expGen(double lambda) {
        return (-(1.0 / lambda)) * Math.log(new Random().nextDouble());
    }

    class listItem {
        public int T1;
        public int T2;
        public double proz;
        public double price;
        public double percent;

        public listItem(int t1, int t2, double proz, double price, double percent) {
            T1 = t1;
            T2 = t2;
            this.proz = proz;
            this.price = price;
            this.percent = percent;
        }

        public boolean equals(listItem l){
            return (T1 == l.T1 && T2 == l.T2 && proz == l.proz && l.price == price && percent == l.percent);
        }
    }
}