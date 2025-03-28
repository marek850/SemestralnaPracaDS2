package com.sem2.UserInterface;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.*;

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

        JLabel timeFactorLabel = new JLabel("Výber stratégie:");
        String[] timeFactors = {"Skutočný čas", "0.5x Skutočný čas", "MAX rýchlosť"};
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
        animationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel tab3 = new JPanel();
        tab3.add(new JLabel("Obsah tretej záložky"));

        tabbedPane.addTab("Graf", chartPanel);
        tabbedPane.addTab("Animácia", animationPanel);
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
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));
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
        setVisible(true);

    
    }

    private Object pauseSimulation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pauseSimulation'");
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
            switch (timeFactorString) {
                case "Skutočný čas":
                    simulation.setRealTimeMode();
                    break;
                case "0.5x Skutočný čas":
                    simulation.setFastTimeMode();;
                    break;
                case "MAX rýchlosť":
                    simulation.setMaxSpeedMode();
                    break;
                default:
                    break;
            }
            simulation.addUserInterface(this);
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
        employeesPanel.removeAll();
        ordersPanel.removeAll();
        assemblyStationsPanel.removeAll();

        // Aktualizácia zamestnancov
        ArrayList<Employee> allEmployees = new ArrayList<>();
        allEmployees.addAll(simulation.getEmployeesA());
        allEmployees.addAll(simulation.getEmployeesB());
        allEmployees.addAll(simulation.getEmployeesC());
        for (Employee employee : allEmployees) {
            employeesPanel.add(createStyledLabel("Employee: " + employee.getType() + " - " + employee.getState()));
        }

        // Aktualizácia objednávok
        for (Order order : simulation.getAllActiveOrders()) {
            ordersPanel.add(createStyledLabel("Order: " + order.getID() + " - " + order.getState()));
        }

        // Aktualizácia montážnych miest
        for (AssemblyStation station : simulation.getAllAssemblyStations()) {
            assemblyStationsPanel.add(createStyledLabel("Assembly Station: " + station.getId() + " - " + station.getCurrentProcess()));
        }

        employeesPanel.revalidate();
        employeesPanel.repaint();
        ordersPanel.revalidate();
        ordersPanel.repaint();
        assemblyStationsPanel.revalidate();
        assemblyStationsPanel.repaint();
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


/* 

public class SimulationGUI extends JFrame implements UserInterface {
    private FurnitureCompany simulation;
    private JLabel simTimeLabel;
    private JPanel employeesPanel;
    private JPanel ordersPanel;
    private JPanel assemblyStationsPanel;
    private Timer updateTimer;
    private JButton startButton;
    private JButton stopButton;

    public SimulationGUI() {
        setTitle("Furniture Company Simulation");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Hlavný panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Simulačný čas
        simTimeLabel = new JLabel("Simulation Time: 0", SwingConstants.CENTER);
        simTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(simTimeLabel, BorderLayout.NORTH);

        // Obsahový panel s rozložením
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

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

        // Panel s tlačidlami
        JPanel buttonPanel = new JPanel(new FlowLayout());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(60, 179, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
        buttonPanel.add(startButton);

        stopButton = new JButton("Stop Simulation");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(220, 20, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });
        buttonPanel.add(stopButton);
    }
    
    private void startSimulation() {
        simulation = new FurnitureCompany(7171200, 5, 5, 5);
        simulation.addUserInterface(this);
        new Thread(() -> simulation.runSimulation(1000)).start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopSimulation() {
        
        simulation = null;
        simTimeLabel.setText("Simulation Time: 0");
        employeesPanel.removeAll();
        ordersPanel.removeAll();
        assemblyStationsPanel.removeAll();

        employeesPanel.revalidate();
        employeesPanel.repaint();
        ordersPanel.revalidate();
        ordersPanel.repaint();
        assemblyStationsPanel.revalidate();
        assemblyStationsPanel.repaint();

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void updateGUI() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        if (simulation == null) return;
        simTimeLabel.setText("Simulation Time: " + simulation.getCurrentTime());
        employeesPanel.removeAll();
        ordersPanel.removeAll();
        assemblyStationsPanel.removeAll();

        // Aktualizácia zamestnancov
        ArrayList<Employee> allEmployees = new ArrayList<>();
        allEmployees.addAll(simulation.getEmployeesA());
        allEmployees.addAll(simulation.getEmployeesB());
        allEmployees.addAll(simulation.getEmployeesC());
        for (Employee employee : allEmployees) {
            employeesPanel.add(createStyledLabel("Employee: " + employee.getType() + " - " + employee.getState()));
        }

        // Aktualizácia objednávok
        for (Order order : simulation.getAllActiveOrders()) {
            ordersPanel.add(createStyledLabel("Order: " + order.getID() + " - " + order.getState()));
        }

        // Aktualizácia montážnych miest
        for (AssemblyStation station : simulation.getAllAssemblyStations()) {
            assemblyStationsPanel.add(createStyledLabel("Assembly Station: " + station.getId() + " - " + station.getCurrentProcess()));
        }

        employeesPanel.revalidate();
        employeesPanel.repaint();
        ordersPanel.revalidate();
        ordersPanel.repaint();
        assemblyStationsPanel.revalidate();
        assemblyStationsPanel.repaint();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationGUI gui = new SimulationGUI();
            gui.setVisible(true);
        });
    }

    @Override
    public void refresh(EventSimulationCore simulationCore) {
        updateGUI();
    }
}
 */