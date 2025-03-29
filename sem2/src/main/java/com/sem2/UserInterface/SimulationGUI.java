package com.sem2.UserInterface;



import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class SimulationGUI extends JFrame implements UserInterface {
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private JButton startButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JTextField aEmployees;
    private JTextField bEmployees;
    private JTextField cEmployees;
    private JTextField repsTextField;
    private JTextField pointsTextField;
    private JTextField currentReplicationTextField;
    private JTextField simulationTimeLabelTextField;
    private JComboBox<String> timeFactorComboBox;
    private JPanel employeesPanel;
    private JPanel ordersPanel;
    private JPanel assemblyStationsPanel;
    private JTextField averageTimeTextField;
    private FurnitureCompany simulation;
    private int points;
    private int totalReplications;
    private JTable employeesTable;
    private JTable ordersTable;
    private JTable assemblyStationsTable;
    private JTable waitingOrderTable;
    private JTable freeWorkerATable;
    private JTable freeWorkerBTable;
    private JTable freeWorkerCTable;
    private JTable freeAssemblyTable;
    private JTable waitingForPaintingTable;
    private JTable waitingForAssemblyTable;
    private JTable waitingForHardwareTable;
    
    public SimulationGUI() {
        setTitle("Magula Semestralna práca");
        dataset = new XYSeriesCollection(new XYSeries("Celkový čas spracovania objednávky"));
        chart = ChartFactory.createXYLineChart(
                "Čas spracovania objednávky", // Názov grafu
                "Replikacie", // X-os
                "Čas(H)", // Y-os
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        /* XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        chart.getXYPlot().setRenderer(renderer); */
        NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        rangeAxis.setAutoRange(true); // Enable auto-ranging
        rangeAxis.setAutoRangeIncludesZero(false); // Exclude zero from the Y-axis range

        chart.setBackgroundPaint(Color.BLACK);
        XYPlot plot = chart.getXYPlot();

        // Nastavenie tmavého pozadia pre graf
        plot.setBackgroundPaint(Color.DARK_GRAY);
        plot.getDomainAxis().setLabelPaint(Color.WHITE); // Os X
        plot.getRangeAxis().setLabelPaint(Color.WHITE); // Os Y
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE); // Os X
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE); // Os Y
        // Nastavenie farby mriežky na tmavú (šedá)
        plot.setDomainGridlinePaint(Color.GRAY); // Gridline pre os X
        plot.setRangeGridlinePaint(Color.GRAY);   // Gridline pre os Y

        // Nastavenie farby čiar na biele alebo svetlé pre lepšiu viditeľnosť
        plot.getRenderer().setSeriesPaint(0, Color.GREEN); // Pre prvú sériu dát
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        // Nastavenie farby pre legendu
        chart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        chart.getLegend().setItemPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Pole pre zadanie počtu replikácií
        JLabel repsLabel = new JLabel("Počet replikácií:");
        repsTextField = new JTextField(10);

        JLabel aEmpLabel = new JLabel("Počet zamestnancov A:");
        aEmployees = new JTextField(10);

        JLabel bEmpLabel = new JLabel("Počet zamestnancov B:");
        bEmployees = new JTextField(10);

        JLabel cEmpLabel = new JLabel("Počet zamestnancov C:");
        cEmployees = new JTextField(10);

        // Pole pre zadanie počtu bodov na grafe
        JLabel pointsLabel = new JLabel("Počet bodov vykreslených na grafe:");
        pointsTextField = new JTextField(10);

        // Nové polia pre aktuálnu replikáciu a priemerné náklady
        JLabel currentReplicationLabel = new JLabel("Aktuálna replikácia:");
        currentReplicationTextField = new JTextField(10);
        currentReplicationTextField.setEditable(false); // Zakázanie editácie

        JLabel simulationTimeLabel = new JLabel("Simulačný čas:");
        simulationTimeLabelTextField = new JTextField(20);
        simulationTimeLabelTextField.setEditable(false); // Zakázanie editácie

        JLabel timeFactorLabel = new JLabel("Zrýchlenie voči realite:");
        String[] timeFactors = {"0x", "2x", "5x", "10x", "20x", "50x", "100x", "MAX rýchlosť"};
        timeFactorComboBox = new JComboBox<>(timeFactors);
        
        // Tlačidlá
        startButton = new JButton("Spustiť");
        stopButton = new JButton("Zastaviť");
        stopButton.setEnabled(false);
        pauseButton = new JButton("Pozastaviť");
        pauseButton.addActionListener(e -> pauseSimulation());
        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel animationPanel = new JPanel(new BorderLayout(10, 10));
        JScrollPane animationPanelScroll = new JScrollPane(animationPanel);
        animationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel tab3 = new JPanel();
        tab3.add(new JLabel("Obsah tretej záložky"));

        tabbedPane.addTab("Graf", chartPanel);
        tabbedPane.addTab("Animácia", animationPanelScroll);
        tabbedPane.addTab("Štatistiky", tab3);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Komponenty sa roztiahnu vodorovne
        gbc.insets = new Insets(5, 5, 5, 5); // Odsadenie medzi prvkami (hore, vľavo, dole, vpravo)

        // Prvý riadok
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(repsLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(repsTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(pointsLabel, gbc);
        gbc.gridx = 3;
        controlPanel.add(pointsTextField, gbc);
        // Druhý riadok
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(aEmpLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(aEmployees, gbc);
        gbc.gridx = 2;
        controlPanel.add(bEmpLabel, gbc);
        gbc.gridx = 3;
        controlPanel.add(bEmployees, gbc);
        gbc.gridx = 4;
        controlPanel.add(cEmpLabel, gbc);
        gbc.gridx = 5;
        controlPanel.add(cEmployees, gbc);
        // Tretí riadok
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(timeFactorLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(timeFactorComboBox, gbc);


        // Tretí riadok
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(startButton, gbc);

        gbc.gridx = 1;
        controlPanel.add(stopButton, gbc);

        gbc.gridx = 2;
        controlPanel.add(pauseButton, gbc);

        

        // Štvrtý riadok
        gbc.gridx = 0;
        gbc.gridy = 4;
        controlPanel.add(currentReplicationLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(currentReplicationTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(simulationTimeLabel, gbc);

        gbc.gridx = 3;
        controlPanel.add(simulationTimeLabelTextField, gbc);


        JPanel statsPanel = new JPanel();
        JLabel averageTime = new JLabel("Čas spracovania objednávky:");
        averageTimeTextField = new JTextField(10);
        averageTimeTextField.setEditable(false); // Zakázanie editácie
        statsPanel.add(averageTime);
        statsPanel.add(averageTimeTextField);

        // Obsahový panel s rozložením
        /* JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        animationPanel.add(contentPanel, BorderLayout.CENTER);

        // Zamestnanci sekcia
        employeesPanel = new JPanel();
        employeesPanel.setLayout(new BoxLayout(employeesPanel, BoxLayout.Y_AXIS));
        JScrollPane employeesScroll = new JScrollPane(employeesPanel);
        employeesScroll.setBorder(BorderFactory.createTitledBorder("Employees"));
        contentPanel.add(employeesScroll);

        // Objednávky sekcia
        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        JScrollPane ordersScroll = new JScrollPane(ordersPanel);
        ordersScroll.setBorder(BorderFactory.createTitledBorder("Orders"));
        contentPanel.add(ordersScroll);

        // Montážne miesta sekcia
        assemblyStationsPanel = new JPanel();
        assemblyStationsPanel.setLayout(new BoxLayout(assemblyStationsPanel, BoxLayout.Y_AXIS));
        JScrollPane assemblyStationsScroll = new JScrollPane(assemblyStationsPanel);
        assemblyStationsScroll.setBorder(BorderFactory.createTitledBorder("Assembly Stations"));
        contentPanel.add(assemblyStationsScroll);

        setLayout(new BorderLayout());
        
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); */
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        animationPanel.add(contentPanel, BorderLayout.CENTER);

        // Sekcia so zamestnancami s JTable
        String[] employeeColumns = {"Skupina", "Pozícia", "Stav"};
        Object[][] employeeData = {};
        employeesTable = new JTable(new DefaultTableModel(employeeData, employeeColumns));
        JScrollPane employeesScroll = new JScrollPane(employeesTable);
        employeesScroll.setBorder(BorderFactory.createTitledBorder("Činnosti zamestnancov"));
        contentPanel.add(employeesScroll);

        // Sekcia s objednávkami s JTable
        String[] orderColumns = {"ID", "Typ", "Stav"};
        Object[][] orderData = {};
        ordersTable = new JTable(new DefaultTableModel(orderData, orderColumns));
        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setBorder(BorderFactory.createTitledBorder("Všetky objednávky"));
        contentPanel.add(ordersScroll);

        // Sekcia s montážnymi miestami s JTable
        String[] assemblyColumns = {"ID", "Stav"};
        Object[][] assemblyData = {};
        assemblyStationsTable = new JTable(new DefaultTableModel(assemblyData, assemblyColumns));
        JScrollPane assemblyStationsScroll = new JScrollPane(assemblyStationsTable);
        assemblyStationsScroll.setBorder(BorderFactory.createTitledBorder("ˇČinnosti na montážnych staniciach"));
        contentPanel.add(assemblyStationsScroll);

        // Pridanie frontsPanel do contentPanel
        JPanel frontsPanel = new JPanel(new GridLayout(2, 4, 10, 10)); // Rozdelenie na 2 riadky a 4 stĺpce
        animationPanel.add(frontsPanel, BorderLayout.NORTH);

        // Cakajúce objednávky
        String[] waitingOrderColumns = {"ID", "Typ", "Stav"};
        Object[][] waitingOrderData = {};
        waitingOrderTable = new JTable(new DefaultTableModel(waitingOrderData, waitingOrderColumns));
        JScrollPane waitingOrderScroll = new JScrollPane(waitingOrderTable);
        waitingOrderScroll.setBorder(BorderFactory.createTitledBorder("Cakajúce objednávky"));
        frontsPanel.add(waitingOrderScroll);

        // Voľní pracovníci A
        String[] freeWorkerAColumns = {"Skupina", "Pozícia", "Stav"};
        Object[][] freeWorkerAData = {};
        freeWorkerATable = new JTable(new DefaultTableModel(freeWorkerAData, freeWorkerAColumns));
        JScrollPane freeWorkerAScroll = new JScrollPane(freeWorkerATable);
        freeWorkerAScroll.setBorder(BorderFactory.createTitledBorder("Voľní pracovníci A"));
        frontsPanel.add(freeWorkerAScroll);

        // Voľní pracovníci B
        String[] freeWorkerBColumns = {"Skupina", "Pozícia", "Stav"};
        Object[][] freeWorkerBData = {};
        freeWorkerBTable = new JTable(new DefaultTableModel(freeWorkerBData, freeWorkerBColumns));
        JScrollPane freeWorkerBScroll = new JScrollPane(freeWorkerBTable);
        freeWorkerBScroll.setBorder(BorderFactory.createTitledBorder("Voľní pracovníci B"));
        frontsPanel.add(freeWorkerBScroll);

        // Voľní pracovníci C
        String[] freeWorkerCColumns = {"Skupina", "Pozícia", "Stav"};
        Object[][] freeWorkerCData = {};
        freeWorkerCTable = new JTable(new DefaultTableModel(freeWorkerCData, freeWorkerCColumns));
        JScrollPane freeWorkerCScroll = new JScrollPane(freeWorkerCTable);
        freeWorkerCScroll.setBorder(BorderFactory.createTitledBorder("Voľní pracovníci C"));
        frontsPanel.add(freeWorkerCScroll);

        // Voľné montážne miesta
        String[] freeAssemblyColumns = {"ID", "Stav"};
        Object[][] freeAssemblyData = {};
        freeAssemblyTable = new JTable(new DefaultTableModel(freeAssemblyData, freeAssemblyColumns));
        JScrollPane freeAssemblyScroll = new JScrollPane(freeAssemblyTable);
        freeAssemblyScroll.setBorder(BorderFactory.createTitledBorder("Voľné montážne miesta"));
        frontsPanel.add(freeAssemblyScroll);

        // Objednávky čakajúce na lakovanie
        String[] waitingForPaintingColumns = {"ID", "Typ", "Stav"};
        Object[][] waitingForPaintingData = {};
        waitingForPaintingTable = new JTable(new DefaultTableModel(waitingForPaintingData, waitingForPaintingColumns));
        JScrollPane waitingForPaintingScroll = new JScrollPane(waitingForPaintingTable);
        waitingForPaintingScroll.setBorder(BorderFactory.createTitledBorder("Objednávky čakajúce na lakovanie"));
        frontsPanel.add(waitingForPaintingScroll);

        // Objednávky čakajúce na skladanie
        String[] waitingForAssemblyColumns = {"ID", "Typ", "Stav"};
        Object[][] waitingForAssemblyData = {};
        waitingForAssemblyTable = new JTable(new DefaultTableModel(waitingForAssemblyData, waitingForAssemblyColumns));
        JScrollPane waitingForAssemblyScroll = new JScrollPane(waitingForAssemblyTable);
        waitingForAssemblyScroll.setBorder(BorderFactory.createTitledBorder("Objednávky čakajúce na skladanie"));
        frontsPanel.add(waitingForAssemblyScroll);

        // Objednávky čakajúce na montáž kovania
        String[] waitingForHardwareColumns = {"ID", "Typ", "Stav"};
        Object[][] waitingForHardwareData = {};
        waitingForHardwareTable = new JTable(new DefaultTableModel(waitingForHardwareData, waitingForHardwareColumns));
        JScrollPane waitingForHardwareScroll = new JScrollPane(waitingForHardwareTable);
        waitingForHardwareScroll.setBorder(BorderFactory.createTitledBorder("Objednávky čakajúce na montáž kovania"));
        frontsPanel.add(waitingForHardwareScroll);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    
    }

    private void pauseSimulation() {
        if (this.pauseButton.getText() == "Pozastaviť") {
            this.simulation.setPause(true);
            this.pauseButton.setText("Pokračovať");
        } else if (this.pauseButton.getText() == "Pokračovať") {
            this.simulation.setPause(false);
            this.pauseButton.setText("Pozastaviť");
        }
    }

    private void startSimulation() {
        this.totalReplications = Integer.parseInt(repsTextField.getText());
        /* if (totalReplications == 1) {
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis xAxis = plot.getDomainAxis();
            xAxis.setLabel("Dni");
        } else {
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis xAxis = plot.getDomainAxis();
            xAxis.setLabel("Replikácie");
        } */
        if (pointsTextField.getText() != null && !pointsTextField.getText().isEmpty()) {
            this.points = Integer.parseInt(pointsTextField.getText());
        }
        int aEmployees = Integer.parseInt(this.aEmployees.getText());
        int bEmployees = Integer.parseInt(this.bEmployees.getText());
        int cEmployees = Integer.parseInt(this.cEmployees.getText());
        String timeFactorString = (String) timeFactorComboBox.getSelectedItem();
        
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        dataset.getSeries("Celkový čas spracovania objednávky").clear();
        Thread thread = new Thread(() -> {
            simulation = new FurnitureCompany(7171200, aEmployees, bEmployees, cEmployees);
            if (timeFactorString.equals("MAX rýchlosť")) {
                simulation.setMaxSpeedMode();
            } else{
                String number = extractNumber(timeFactorString);
                if (number != null) {
                    Integer timeFactor = Integer.parseInt(number);
                    if (timeFactor == 0) {
                        simulation.setTimeFactor(1.0);
                    }else{
                        simulation.setTimeFactor((1.0/timeFactor));
                    }
                } 
            }
            simulation.addUserInterface(this);
            simulation.runSimulation(totalReplications);
            enableStartButton();
            disableStopButton();
        }); 
        thread.start();   
    }  
    public static String extractNumber(String str) {
        Pattern pattern = Pattern.compile("(\\d+)(?=x)"); // Regulárny výraz na nájdenie čísla pred 'x'
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            return matcher.group(1); 
        }
        
        return null; 
    }
    public void enableStartButton() {
        startButton.setEnabled(true);
    }

    public void enableStopButton() {
        stopButton.setEnabled(true);
    }

    public void disableStartButton() {
        startButton.setEnabled(false);
    }

    public void disableStopButton() {
        stopButton.setEnabled(false);
    }
    private void stopSimulation() {
        simulation.setStop(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
    private void updateGUI() {
        /* try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } */
        
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }
    @Override
    public void refresh(EventSimulationCore simulationCore) {
        FurnitureCompany simulation = (FurnitureCompany) simulationCore;
        if (simulation == null) return;
        int replicationsToSkip = (int) (totalReplications * 0.3);
        int reps = simulation.getOrderFinishTime().getCount();
        if (reps >= replicationsToSkip) {
            if (totalReplications > points) {
                if (reps % (totalReplications/points) == 0) { 
                    dataset.getSeries("Celkový čas spracovania objednávky").add(reps, simulation.getOrderFinishTime().getAverage());
                }
            }
            else {
                dataset.getSeries("Celkový čas spracovania objednávky").add(reps, simulation.getOrderFinishTime().getAverage());
            }
            
        }
        currentReplicationTextField.setText(String.valueOf(reps + 1));
        int SECONDS_IN_DAY = 86400;
        int SECONDS_IN_WEEK = 5 * SECONDS_IN_DAY;

        // Získaj týždeň
        int week = (int) (simulation.getCurrentTime() / SECONDS_IN_WEEK);

        // Získaj zostávajúci čas v rámci týždňa
        double remainingTime = simulation.getCurrentTime() % SECONDS_IN_WEEK;

        // Získaj deň v rámci týždňa
        int day = (int) (remainingTime / SECONDS_IN_DAY);

        // Získaj čas v rámci dňa
        double timeInDay = remainingTime % SECONDS_IN_DAY;

        // Rozdelenie času na hodiny, minúty a sekundy
        int hours = (int) (timeInDay / 3600);
        int minutes = (int) ((timeInDay % 3600) / 60);
        int seconds = (int) (timeInDay % 60);

        String[] daysOfTheWeek = {"Pondelok", "Utorok", "Streda", "Štvrtok", "Piatok"};

        // Nastav text na požiadavku
        simulationTimeLabelTextField.setText("Týždeň: " + (week + 1) + ", Deň: " + daysOfTheWeek[day] + ", Čas: " 
                                            + String.format("%02d:%02d:%02d", hours, minutes, seconds));
        
        ArrayList<Employee> allEmployees = new ArrayList<>();
        allEmployees.addAll(simulation.getEmployeesA());
        allEmployees.addAll(simulation.getEmployeesB());
        allEmployees.addAll(simulation.getEmployeesC());

        // Vyčistíme existujúce údaje v tabuľke a pridáme nové
        DefaultTableModel employeeTableModel = (DefaultTableModel) employeesTable.getModel();
        employeeTableModel.setRowCount(0); // Odstráni existujúce riadky
        for (Employee employee : allEmployees) {
            employeeTableModel.addRow(new Object[]{employee.getType(), employee.getCurrentPosition(), employee.getState()});
        }

        // Aktualizácia objednávok
        DefaultTableModel orderTableModel = (DefaultTableModel) ordersTable.getModel();
        orderTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Order order : simulation.getAllActiveOrders()) {
            orderTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState()});
        }

        // Aktualizácia montážnych miest
        DefaultTableModel assemblyTableModel = (DefaultTableModel) assemblyStationsTable.getModel();
        assemblyTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (AssemblyStation station : simulation.getAllAssemblyStations()) {
            assemblyTableModel.addRow(new Object[]{station.getId(), station.getCurrentProcess()});
        }
        // Aktualizácia frontov
        DefaultTableModel waitingOrderTableModel = (DefaultTableModel) waitingOrderTable.getModel();
        waitingOrderTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Order order : simulation.getWaitingOrdersQueue()) {
            waitingOrderTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState()});
        }
        // Aktualizácia voľných pracovníkov A
        DefaultTableModel freeWorkerATableModel = (DefaultTableModel) freeWorkerATable.getModel();
        freeWorkerATableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Employee employee : simulation.getAvailableEmployeesA()) {
            freeWorkerATableModel.addRow(new Object[]{employee.getType(), employee.getCurrentPosition(), employee.getState()});
        }
        // Aktualizácia voľných pracovníkov B
        DefaultTableModel freeWorkerBTableModel = (DefaultTableModel) freeWorkerBTable.getModel();
        freeWorkerBTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Employee employee : simulation.getAvailableEmployeesB()) {
            freeWorkerBTableModel.addRow(new Object[]{employee.getType(), employee.getCurrentPosition(), employee.getState()});
        }
        // Aktualizácia voľných pracovníkov C
        DefaultTableModel freeWorkerCTableModel = (DefaultTableModel) freeWorkerCTable.getModel();
        freeWorkerCTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Employee employee : simulation.getAvailableEmployeesC()) {
            freeWorkerCTableModel.addRow(new Object[]{employee.getType(), employee.getCurrentPosition(), employee.getState()});
        }
        // Aktualizácia voľných montážnych miest
        DefaultTableModel freeAssemblyTableModel = (DefaultTableModel) freeAssemblyTable.getModel();
        freeAssemblyTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (AssemblyStation station : simulation.getAvailableAssemblyStations()) {
            freeAssemblyTableModel.addRow(new Object[]{station.getId(), station.getCurrentProcess()});
        }
        // Aktualizácia objednávok čakajúcich na lakovanie
        DefaultTableModel waitingForPaintingTableModel = (DefaultTableModel) waitingForPaintingTable.getModel();
        waitingForPaintingTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Order order : simulation.getVarnishingWaitQueue()) {
            waitingForPaintingTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState()});
        }
        // Aktualizácia objednávok čakajúcich na skladanie
        DefaultTableModel waitingForAssemblyTableModel = (DefaultTableModel) waitingForAssemblyTable.getModel();
        waitingForAssemblyTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Order order : simulation.getAssemblingWaitQueue()) {
            waitingForAssemblyTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState()});
        }
        // Aktualizácia objednávok čakajúcich na montáž kovania
        DefaultTableModel waitingForHardwareTableModel = (DefaultTableModel) waitingForHardwareTable.getModel();
        waitingForHardwareTableModel.setRowCount(0); // Vymaže existujúce riadky
        for (Order order : simulation.getFittingWaitQueue()) {
            waitingForHardwareTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState()});
        }

        // Obnovíme zobrazenie v tabuľkách
        employeesTable.revalidate();
        employeesTable.repaint();
        ordersTable.revalidate();
        ordersTable.repaint();
        assemblyStationsTable.revalidate();
        assemblyStationsTable.repaint();
        waitingOrderTable.revalidate();
        waitingOrderTable.repaint();
        freeWorkerATable.revalidate();
        freeWorkerATable.repaint();
        freeWorkerBTable.revalidate();
        freeWorkerBTable.repaint();
        freeWorkerCTable.revalidate();
        freeWorkerCTable.repaint();
        freeAssemblyTable.revalidate();
        freeAssemblyTable.repaint();
        waitingForPaintingTable.revalidate();
        waitingForPaintingTable.repaint();
        waitingForAssemblyTable.revalidate();
        waitingForAssemblyTable.repaint();
        waitingForHardwareTable.revalidate();
        waitingForHardwareTable.repaint();
        
        
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new SimulationGUI().setVisible(true);
        });
    }
}

