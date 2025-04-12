package com.sem2.UserInterface;



import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.Position;
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
    private FurnitureCompany simulation;
    private int points;
    private int totalReplications;
    private JTable employeesTable;
    private JTable ordersTable;
    private JTable assemblyStationsTable;
    private JTextField waitingOrderLabelTextField;
    private JTextField freeALabelLabelTextField;
    private JTextField freeBLabelLabelTextField;
    private JTextField freeCLabelLabelTextField;
    private JTextField freeStationsLabelTextField;
    private JTextField waitingForVarnishLabelTextField;
    private JTextField waitingForAssembleLabelTextField;
    private JTextField waitingForFittingLabelTextField;
    private JTable orderStatsJTable;
    private JTable groupStatsJTable;
    private JTable employeeStatsJTable;

    public SimulationGUI() {
        setTitle("Magula Semestralna práca");
        dataset = new XYSeriesCollection(new XYSeries("Celkový čas spracovania objednávky"));
        chart = ChartFactory.createXYLineChart(
                "Čas spracovania objednávky", // Názov grafu
                "Replikacie", // X-os
                "Čas(s)", // Y-os
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
        plot.getRenderer().setSeriesPaint(0, Color.MAGENTA); // Pre prvú sériu dát
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

        JLabel timeFactorLabel = new JLabel("Pomer (Simulačný čas / Skutočný čas) = 1:");
        String[] timeFactors = {"0.5", "1", "2", "5", "10", "50", "100", "1000","MAX rýchlosť"};
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
        JPanel statisticsPanel = new JPanel();
        JScrollPane statisticsScrollPane = new JScrollPane(statisticsPanel);
        
        tabbedPane.addTab("Graf", chartPanel);
        tabbedPane.addTab("Animácia", animationPanelScroll);
        tabbedPane.addTab("Štatistiky", statisticsScrollPane);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(5, 5, 5, 5); 

       
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(repsLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(repsTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(pointsLabel, gbc);
        gbc.gridx = 3;
        controlPanel.add(pointsTextField, gbc);
        
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
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(timeFactorLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(timeFactorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(startButton, gbc);

        gbc.gridx = 1;
        controlPanel.add(stopButton, gbc);

        gbc.gridx = 2;
        controlPanel.add(pauseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        controlPanel.add(currentReplicationLabel, gbc);

        gbc.gridx = 1;
        controlPanel.add(currentReplicationTextField, gbc);
        gbc.gridx = 2;
        controlPanel.add(simulationTimeLabel, gbc);

        gbc.gridx = 3;
        controlPanel.add(simulationTimeLabelTextField, gbc);


        

        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        animationPanel.add(contentPanel, BorderLayout.CENTER);

        
        String[] employeeColumns = {"ID", "Skupina", "Pozícia", "Stav", "Montazne miesto"};
        Object[][] employeeData = {};
        employeesTable = new JTable(new DefaultTableModel(employeeData, employeeColumns));
        JScrollPane employeesScroll = new JScrollPane(employeesTable);
        employeesScroll.setBorder(BorderFactory.createTitledBorder("Činnosti zamestnancov"));
        contentPanel.add(employeesScroll);

        
        String[] orderColumns = {"ID", "Typ", "Stav", "Montazne miesto"};
        Object[][] orderData = {};
        ordersTable = new JTable(new DefaultTableModel(orderData, orderColumns));
        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setBorder(BorderFactory.createTitledBorder("Všetky objednávky"));
        contentPanel.add(ordersScroll);

        
        String[] assemblyColumns = {"ID", "Stav"};
        Object[][] assemblyData = {};
        assemblyStationsTable = new JTable(new DefaultTableModel(assemblyData, assemblyColumns));
        JScrollPane assemblyStationsScroll = new JScrollPane(assemblyStationsTable);
        assemblyStationsScroll.setBorder(BorderFactory.createTitledBorder("ˇČinnosti na montážnych staniciach"));
        contentPanel.add(assemblyStationsScroll);

        
        JPanel frontsPanel = new JPanel(new GridLayout(2, 4, 10, 10)); 
        animationPanel.add(frontsPanel, BorderLayout.NORTH);

       
        JLabel waitingOrderLabel = new JLabel("Objednávky čakajúce na rezanie:");
        waitingOrderLabelTextField = new JTextField(10);
        waitingOrderLabelTextField.setEditable(false);

        frontsPanel.add(waitingOrderLabel);
        frontsPanel.add(waitingOrderLabelTextField);

        // Voľní pracovníci A
        JLabel freeALabel = new JLabel("Voľní pracovníci skupiny A:");
        freeALabelLabelTextField = new JTextField(10);
        freeALabelLabelTextField.setEditable(false);
        frontsPanel.add(freeALabel);
        frontsPanel.add(freeALabelLabelTextField);

        // Voľní pracovníci B
        JLabel freeBLabel = new JLabel("Voľní pracovníci skupiny B:");
        freeBLabelLabelTextField = new JTextField(10);
        freeBLabelLabelTextField.setEditable(false);
        frontsPanel.add(freeBLabel);
        frontsPanel.add(freeBLabelLabelTextField);


        // Voľní pracovníci C
        JLabel freeCLabel = new JLabel("Voľní pracovníci skupiny C:");
        freeCLabelLabelTextField = new JTextField(10);
        freeCLabelLabelTextField.setEditable(false);
        frontsPanel.add(freeCLabel);
        frontsPanel.add(freeCLabelLabelTextField);


        // Voľné montážne miesta
        JLabel freeStationsLabel = new JLabel("Voľné montážne miesta:");
        freeStationsLabelTextField = new JTextField(10);
        freeStationsLabelTextField.setEditable(false);
        frontsPanel.add(freeStationsLabel);
        frontsPanel.add(freeStationsLabelTextField);


        // Objednávky čakajúce na lakovanie
        JLabel waitingForVarnishLabel = new JLabel("Objednávky čakajúce na lakovanie:");
        waitingForVarnishLabelTextField = new JTextField(10);
        waitingForVarnishLabelTextField.setEditable(false);
        frontsPanel.add(waitingForVarnishLabel);
        frontsPanel.add(waitingForVarnishLabelTextField);


        // Objednávky čakajúce na skladanie
        JLabel waitingForAssembleLabel = new JLabel("Objednávky čakajúce na skladanie:");
        waitingForAssembleLabelTextField = new JTextField(10);
        waitingForAssembleLabelTextField.setEditable(false);
        frontsPanel.add(waitingForAssembleLabel);
        frontsPanel.add(waitingForAssembleLabelTextField);

        // Objednávky čakajúce na montáž kovania
        JLabel waitingForFittingLabel = new JLabel("Objednávky čakajúce na montáž kovani:");
        waitingForFittingLabelTextField = new JTextField(10);
        waitingForFittingLabelTextField.setEditable(false);
        frontsPanel.add(waitingForFittingLabel);
        frontsPanel.add(waitingForFittingLabelTextField);

        String[] ordetStatsCol = {"Štatistika", "Hodnota", "Interval Spolahlivosti"};
        Object[][] orderStatsData = {};
        orderStatsJTable = new JTable(new DefaultTableModel(orderStatsData, ordetStatsCol));
        JScrollPane orderStatScrollPane = new JScrollPane(orderStatsJTable);
        orderStatScrollPane.setBorder(BorderFactory.createTitledBorder("Statistiky objednávok"));
        statisticsPanel.add(orderStatScrollPane);

        String[] groupStatsCol = {"Skupina", "Vyťaženosť(%)", "Interval Spolahlivosti"};
        Object[][] groupStatsData = {};
        groupStatsJTable = new JTable(new DefaultTableModel(groupStatsData, groupStatsCol));
        JScrollPane groupStatScrollPane = new JScrollPane(groupStatsJTable);
        groupStatScrollPane.setBorder(BorderFactory.createTitledBorder("Štatistiky skupín zamestnancov"));
        statisticsPanel.add(groupStatScrollPane);

        String[] employeeStatsCol = {"ID","Skupina", "Vyťaženosť(%)", "Interval Spolahlivosti"};
        Object[][] employeeStatsData = {};
        employeeStatsJTable = new JTable(new DefaultTableModel(employeeStatsData, employeeStatsCol));
        JScrollPane employeeStatScrollPane = new JScrollPane(employeeStatsJTable);
        employeeStatScrollPane.setBorder(BorderFactory.createTitledBorder("Štatistiky zamestnancov"));
        statisticsPanel.add(employeeStatScrollPane);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

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
        
            simulation = new FurnitureCompany(7171200, aEmployees, bEmployees, cEmployees);
            if (timeFactorString.equals("MAX rýchlosť")) {
                simulation.setTimeFactor(0.0);
            } else{
                Integer timeFactor = Integer.parseInt(timeFactorString);
                simulation.setTimeFactor((double)timeFactor);
            }
            simulation.addUserInterface(this);
        Thread thread = new Thread(() -> {
            simulation.runSimulation(totalReplications);
            enableStartButton();
            disableStopButton();
        }); 
        thread.start();   
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
    

    @Override
    public void refresh(EventSimulationCore simulationCore) {
        FurnitureCompany simulation = (FurnitureCompany) simulationCore;
        if (simulation == null) return;
        int replicationsToSkip = (int) (totalReplications * 0.3);
        int reps = simulation.getGlobalFinishTime().getCount();
        if (totalReplications > 1) {
           
        
            if (reps >= replicationsToSkip) {
                if (totalReplications > points) {
                    if (reps % (totalReplications/points) == 0) { 
                        
                        dataset.getSeries("Celkový čas spracovania objednávky").add(reps, simulation.getGlobalFinishTime().getAverage());
                        
                    }
                }
                else {
                    dataset.getSeries("Celkový čas spracovania objednávky").add(reps, simulation.getGlobalFinishTime().getAverage());
                }
                
            }
            if (simulation.isDone()) {
                DefaultTableModel orderStatsModel = (DefaultTableModel) orderStatsJTable.getModel();
                orderStatsModel.setRowCount(0); 
                orderStatsModel.addRow(new Object[]{"Priemerný čas spracovania objednávky(h)", String.format("%.4f", simulation.getGlobalFinishTime().getAverage()/3600), "<" + String.format("%.4f", simulation.getGlobalFinishTime().getConfidenceInterval95()[0]/3600) + ", " + String.format("%.4f", simulation.getGlobalFinishTime().getConfidenceInterval95()[1]/3600) + ">"});
                orderStatsModel.addRow(new Object[]{"Priemerny pocet cakajucich objednavok:", String.format("%.4f", simulation.getGlobalWaitingOrders().getAverage()), "<" + String.format("%.4f", simulation.getGlobalWaitingOrders().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", simulation.getGlobalWaitingOrders().getConfidenceInterval95()[1]) + ">"});
                DefaultTableModel groupWorkStats = (DefaultTableModel) groupStatsJTable.getModel();
                groupWorkStats.setRowCount(0); 
                groupWorkStats.addRow(new Object[]{"A", String.format("%.4f", simulation.getWorkloadA().getAverage()), "<" + String.format("%.4f", simulation.getWorkloadA().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", simulation.getWorkloadA().getConfidenceInterval95()[1]) + ">"});
                groupWorkStats.addRow(new Object[]{"B", String.format("%.4f", simulation.getWorkloadB().getAverage()), "<" + String.format("%.4f", simulation.getWorkloadB().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", simulation.getWorkloadB().getConfidenceInterval95()[1]) + ">"});
                groupWorkStats.addRow(new Object[]{"C", String.format("%.4f", simulation.getWorkloadC().getAverage()), "<" + String.format("%.4f", simulation.getWorkloadC().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", simulation.getWorkloadC().getConfidenceInterval95()[1]) + ">"});
        
                
                DefaultTableModel employyStatsModel = (DefaultTableModel) employeeStatsJTable.getModel();
                employyStatsModel.setRowCount(0); 
                int index = 0;
                for (Employee employee : simulation.getEmployeesA()) {
                    employyStatsModel.addRow(new Object[]{index, "A", String.format("%.4f", employee.getWorkloadStat().getAverage()), "<" + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[1]) + ">"});
                    index++;
                }
                for (Employee employee : simulation.getEmployeesB()) {
                    employyStatsModel.addRow(new Object[]{index, "B", String.format("%.4f", employee.getWorkloadStat().getAverage()), "<" + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[1]) + ">"}); 
                    index++;
                }
                for (Employee employee : simulation.getEmployeesC()) {
                    employyStatsModel.addRow(new Object[]{index, "C", String.format("%.4f", employee.getWorkloadStat().getAverage()), "<" + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[0]) + ", " + String.format("%.4f", employee.getWorkloadStat().getConfidenceInterval95()[1]) + ">"});
                    index++;
                }
            }
        }   else{
            
            String timeFactorString = (String) timeFactorComboBox.getSelectedItem();
            Integer timeFactor;
            if (timeFactorString.equals("MAX rýchlosť")) {
                timeFactor = 0;
            } else {
                timeFactor = Integer.parseInt(timeFactorString);
            }
            if(simulation.getTimeFactor() != timeFactor) {
                simulation.setTimeFactor(timeFactor.doubleValue());
            }
        
            currentReplicationTextField.setText(String.valueOf(reps + 1));
            int SECONDS_IN_DAY = 28800;
            int SECONDS_IN_WEEK = 5 * SECONDS_IN_DAY;
    
            
            int week = (int) (simulation.getCurrentTime() / SECONDS_IN_WEEK);
    
            double remainingTime = simulation.getCurrentTime() % SECONDS_IN_WEEK;

            int day = (int) (remainingTime / SECONDS_IN_DAY);
    
            double timeInDay = remainingTime % SECONDS_IN_DAY;
    
            int hours = (int) (timeInDay / 3600);
            int minutes = (int) ((timeInDay % 3600) / 60);
            int seconds = (int) (timeInDay % 60);
    
            String[] daysOfTheWeek = {"Pondelok", "Utorok", "Streda", "Štvrtok", "Piatok"};
                simulationTimeLabelTextField.setText("Týždeň: " + (week + 1) + ", Deň: " + daysOfTheWeek[day] + ", Čas: " 
                                                    + String.format("%02d:%02d:%02d", hours, minutes, seconds));
            ArrayList<Employee> allEmployees = new ArrayList<>();
            allEmployees.addAll(simulation.getEmployeesA());
            allEmployees.addAll(simulation.getEmployeesB());
            allEmployees.addAll(simulation.getEmployeesC());
            SwingUtilities.invokeLater(() -> {
            DefaultTableModel employeeTableModel = (DefaultTableModel) employeesTable.getModel();
            employeeTableModel.setRowCount(0); 
            for (Employee employee : allEmployees) {
                
                employeeTableModel.addRow(new Object[]{employee.getId(),employee.getType(), employee.getCurrentPosition(), employee.getState(), employee.getCurrentPosition() == Position.STORAGE ? "" : employee.getStation().getId()});
            }

            // Aktualizácia objednávok
            DefaultTableModel orderTableModel = (DefaultTableModel) ordersTable.getModel();
            orderTableModel.setRowCount(0); 
            for (Order order : simulation.getAllActiveOrders()) {
                orderTableModel.addRow(new Object[]{order.getID(), order.getType(), order.getState(), order.getStation()==null? "" : order.getStation().getId()});
            }

            // Aktualizácia montážnych miest
            DefaultTableModel assemblyTableModel = (DefaultTableModel) assemblyStationsTable.getModel();
            assemblyTableModel.setRowCount(0); 
            for (AssemblyStation station : simulation.getAllAssemblyStations()) {
                assemblyTableModel.addRow(new Object[]{station.getId(), station.getCurrentProcess()});
            }
            // Aktualizácia frontov
            waitingOrderLabelTextField.setText(String.valueOf(simulation.getWaitingOrdersQueue().size()));
            freeALabelLabelTextField.setText(String.valueOf(simulation.getAvailableEmployeesA().size()));
            freeBLabelLabelTextField.setText(String.valueOf(simulation.getAvailableEmployeesB().size()));
            freeCLabelLabelTextField.setText(String.valueOf(simulation.getAvailableEmployeesC().size()));
            freeStationsLabelTextField.setText(String.valueOf(simulation.getAvailableAssemblyStations().size()));
            waitingForVarnishLabelTextField.setText(String.valueOf(simulation.getVarnishingWaitQueue().size()));
            waitingForAssembleLabelTextField.setText(String.valueOf(simulation.getAssemblingWaitQueue().size()));
            waitingForFittingLabelTextField.setText(String.valueOf(simulation.getFittingWaitQueue().size()));

            DefaultTableModel orderStatsModel = (DefaultTableModel) orderStatsJTable.getModel();
                orderStatsModel.setRowCount(0); 
                orderStatsModel.addRow(new Object[]{"Priemerný čas spracovania objednávky(h)", String.format("%.4f", simulation.getOrderFinishTime().getAverage()), "N/A" });
                orderStatsModel.addRow(new Object[]{"Priemerny pocet cakajucich objednavok:", String.format("%.4f", simulation.getWaitingOrders().getWeightedAverage()), "N/A" });
                DefaultTableModel groupWorkStats = (DefaultTableModel) groupStatsJTable.getModel();
                groupWorkStats.setRowCount(0); 
                
                
                
                
                DefaultTableModel employyStatsModel = (DefaultTableModel) employeeStatsJTable.getModel();
                employyStatsModel.setRowCount(0); 
                int index = 0;
                int count = 0;
                double totalWorkload = 0;
                for (Employee employee : simulation.getEmployeesA()) {
                    totalWorkload += employee.getWorkload(simulation.getCurrentTime());
                    count++;
                    employyStatsModel.addRow(new Object[]{index, "A", String.format("%.4f", employee.getWorkload(simulation.getCurrentTime())), "N/A"});
                    index++;
                }
                groupWorkStats.addRow(new Object[]{"A", String.format("%.4f", totalWorkload/count), "N/A"});
                count = 0;
                totalWorkload = 0;
                for (Employee employee : simulation.getEmployeesB()) {
                    employyStatsModel.addRow(new Object[]{index, "B", String.format("%.4f", employee.getWorkload(simulation.getCurrentTime())), "N/A"});
                    totalWorkload += employee.getWorkload(simulation.getCurrentTime());
                    count++;
                    index++;
                }
                groupWorkStats.addRow(new Object[]{"B", String.format("%.4f", totalWorkload/count), "N/A"});
                count = 0;
                totalWorkload = 0;
                for (Employee employee : simulation.getEmployeesC()) {
                    totalWorkload += employee.getWorkload(simulation.getCurrentTime());
                    count++;
                    employyStatsModel.addRow(new Object[]{index, "C", String.format("%.4f", employee.getWorkload(simulation.getCurrentTime())), "N/A"});
                    index++;
                }
                groupWorkStats.addRow(new Object[]{"C", String.format("%.4f", totalWorkload/count), "N/A"});
        });

    }        
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

