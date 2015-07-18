import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;

import static java.awt.event.KeyEvent.*;

import javax.swing.*;

import static javax.swing.BorderFactory.*;

public class SwingGUI extends JFrame {
	private final JFormattedTextField txtPrisonerCount = new JFormattedTextField(100);
	private final JFormattedTextField txtIterationCount = new JFormattedTextField(10000);
	private final JTextArea txtPrisonerHistory = new JTextArea("0,1,2,3,4,5,6,7,8,9", 6, 30);
	private final JLabel lblCount = new JLabel();
	private final JLabel lblTargetCount = new JLabel();
	private final JLabel lblMinDays = new JLabel();
	private final JLabel lblMinYears = new JLabel();
	private final JLabel lblMaxDays = new JLabel();
	private final JLabel lblMaxYears = new JLabel();
	private final JLabel lblAvgDays = new JLabel();
	private final JLabel lblAvgYears = new JLabel();
	private final JLabel lblStdDevDays = new JLabel();
	private final JLabel lblStdDevYears = new JLabel();
	private final JButton btnRun = new JButton("Futtat");
	private final JButton btnStop = new JButton("Leállít");
	private BackgroundSimulator simulator;
	private final JFormattedTextField txtStage1Length = new JFormattedTextField(2600);
	private final JFormattedTextField txtStage2Length = new JFormattedTextField(2700);
	private final JFormattedTextField txtBulkSize = new JFormattedTextField(11);
	private final ButtonGroup modStrategies = new ButtonGroup();
	private final ButtonGroup modWardenParams = new ButtonGroup();
	private final CardLayout modSpecialParams = new CardLayout();
	private final JPanel uiSpecialParams = new JPanel(modSpecialParams);

	private final ActionListener runListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			try {
				String selectedStrategyName = modStrategies.getSelection().getActionCommand();
				Protocol protocol = createProtocol(selectedStrategyName);

				Warden warden = createWarden();
				int count = warden.hasPredeterminedHistory()? 1 : (int)txtIterationCount.getValue();
				simulator = new BackgroundSimulator(warden, protocol, count);
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(SwingGUI.this, ex.toString());
				ex.printStackTrace();
			}
			if (simulator != null) {
				btnRun.setEnabled(false);
				btnStop.setEnabled(true);
				updateResults(null);
				simulator.execute();
			}
		}
	};
	private final ActionListener strategyListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			//List<String> list = Arrays.asList(ProtocolBulkWithRestart.class.getName());
			//uiSpecialParams.setVisible(list.contains());
			modSpecialParams.show(uiSpecialParams, e.getActionCommand());
		}
	};
	private final ActionListener cancelListener = new ActionListener() {
		@Override public void actionPerformed(ActionEvent e) {
			simulator.cancel(true);
		}
	};

	public SwingGUI() throws HeadlessException {
		super("100 rab és egy lámpa");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel left = new JPanel();
		{
			left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
			left.add(buildStrategies());
			left.add(buildResults());
		}
		getContentPane().add(left, BorderLayout.LINE_START);

		JPanel right = new JPanel();
		{
			right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
			right.add(buildParams());
		}
		getContentPane().add(right, BorderLayout.LINE_END);

		JPanel controls = new JPanel(new FlowLayout());
		{
			btnRun.addActionListener(runListener);
			controls.add(btnRun);

			btnStop.setEnabled(false);
			btnStop.addActionListener(cancelListener);
			controls.add(btnStop);
		}
		getContentPane().add(controls, BorderLayout.PAGE_END);

		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel buildStrategies() {
		JPanel strategies = new JPanel();
		{
			strategies.setLayout(new GridLayout(0, 1));
			strategies.setBorder(createTitledBorder("Stratégiák"));

			strategies.add(createStrategy("Egy lámpaoltogató", VK_L, ProtocolSingleCounter.class));
			strategies.add(createStrategy("Egy lámpaoltogató, okos rabok", VK_O, ProtocolSCWithSmartDrones.class));
			strategies.add(createStrategy("Dinamikusan választott lámpaoltogató", VK_D, ProtocolDynamicCounter.class));
			strategies.add(createStrategy("Kétfázisú számlálás", VK_K, ProtocolBulkWithLoop.class));
			strategies.add(createStrategy("Kétfázisú számlálás (újraindítással)", VK_I, ProtocolBulkWithRestart.class));

			((JRadioButton)strategies.getComponent(0)).setSelected(true);
		}

		return strategies;
	}

	private JRadioButton createStrategy(String text, int mnemonic, Class<?> clazz) {
		JRadioButton strategy = new JRadioButton(text);
		strategy.setMnemonic(mnemonic);
		strategy.setActionCommand(clazz.getName());
		strategy.addActionListener(strategyListener);
		modStrategies.add(strategy);
		return strategy;
	}

	private JPanel buildParams() {
		JPanel params = new JPanel();
		{
			params.setLayout(new BoxLayout(params, BoxLayout.PAGE_AXIS));
			params.setBorder(createTitledBorder("Paraméterek"));

			JPanel iterationCountLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
			{
				JLabel lblIterationCount = new JLabel("Futások száma:");
				lblIterationCount.setDisplayedMnemonic(KeyEvent.VK_F);
				lblIterationCount.setLabelFor(txtIterationCount);
				iterationCountLayout.add(lblIterationCount);
				txtIterationCount.setColumns(6);
				txtIterationCount.setHorizontalAlignment(SwingConstants.RIGHT);
				iterationCountLayout.add(txtIterationCount);
			}
			iterationCountLayout.setAlignmentX(Component.LEFT_ALIGNMENT);
			params.add(iterationCountLayout);

			JPanel prisonerCountLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			{
				JRadioButton prisonerCountRadio = new JRadioButton("Rabok száma:");
				{
					prisonerCountRadio.setMnemonic(KeyEvent.VK_R);
					prisonerCountRadio.setActionCommand("count");
					prisonerCountRadio.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) {
							txtPrisonerCount.setEnabled(true);
							txtPrisonerHistory.setEnabled(false);
						}
					});
					prisonerCountRadio.setSelected(true);
					modWardenParams.add(prisonerCountRadio);
				}
				prisonerCountLayout.add(prisonerCountRadio);

				txtPrisonerCount.setColumns(4);
				txtPrisonerCount.setHorizontalAlignment(SwingConstants.RIGHT);
				prisonerCountLayout.add(txtPrisonerCount);
			}
			prisonerCountLayout.setAlignmentX(Component.LEFT_ALIGNMENT);
			params.add(prisonerCountLayout);

			JRadioButton prisonerHistoryRadio = new JRadioButton("Választott rabok:");
			{
				prisonerHistoryRadio.setMnemonic(KeyEvent.VK_V);
				prisonerHistoryRadio.setActionCommand("history");
				prisonerHistoryRadio.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) {
						txtPrisonerCount.setEnabled(false);
						txtPrisonerHistory.setEnabled(true);
					}
				});
				modWardenParams.add(prisonerHistoryRadio);
			}
			prisonerHistoryRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			params.add(prisonerHistoryRadio);

			txtPrisonerHistory.setEnabled(false);
			JScrollPane scroller = new JScrollPane(txtPrisonerHistory,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroller.setAlignmentX(Component.LEFT_ALIGNMENT);
			params.add(scroller);

			JPanel specialParams = buildSpecialParams();
			specialParams.setAlignmentX(Component.LEFT_ALIGNMENT);
			params.add(specialParams);
		}

		return params;
	}
	private JPanel buildSpecialParams() {
		JPanel bulkProtocolUI = new JPanel(new GridBagLayout());
		{
			bulkProtocolUI.setBorder(createTitledBorder("Speciális paraméterek"));
			bulkProtocolUI.setVisible(false);

			GridBagConstraints gbc;
			JLabel label;

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.ipadx = 8;
			gbc.gridx = 0;

			gbc.gridy = 0;
			label = new JLabel("Első szakasz:");
			label.setLabelFor(txtStage1Length);
			label.setDisplayedMnemonic(KeyEvent.VK_E);
			bulkProtocolUI.add(label, gbc);

			gbc.gridy++;
			label = new JLabel("Második szakasz:");
			label.setLabelFor(txtStage2Length);
			label.setDisplayedMnemonic(KeyEvent.VK_M);
			bulkProtocolUI.add(label, gbc);

			gbc.gridy++;
			label = new JLabel("Lépésköz:");
			label.setLabelFor(txtBulkSize);
			label.setDisplayedMnemonic(KeyEvent.VK_P);
			bulkProtocolUI.add(label, gbc);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridx = 1;

			gbc.gridy = 0;
			bulkProtocolUI.add(txtStage1Length, gbc);
			gbc.gridy++;
			bulkProtocolUI.add(txtStage2Length, gbc);
			gbc.gridy++;
			bulkProtocolUI.add(txtBulkSize, gbc);
		}

		uiSpecialParams.add(bulkProtocolUI);
		Component noExtras = uiSpecialParams.add(new JLabel("Nincsenek extra paraméterek")); // should be last add
		modSpecialParams.addLayoutComponent(noExtras, ProtocolSingleCounter.class.getName());
		modSpecialParams.addLayoutComponent(noExtras, ProtocolDynamicCounter.class.getName());
		modSpecialParams.addLayoutComponent(noExtras, ProtocolSCWithSmartDrones.class.getName());
		modSpecialParams.addLayoutComponent(bulkProtocolUI, ProtocolBulkWithLoop.class.getName());
		modSpecialParams.addLayoutComponent(bulkProtocolUI, ProtocolBulkWithRestart.class.getName());

		return uiSpecialParams;
	}

	private JPanel buildResults() {
		JPanel results = new JPanel(new GridLayout(0, 3, 4, 4));
		results.setBorder(createTitledBorder("Eredmények"));

		((JLabel)results.add(new JLabel("Count"))).setHorizontalAlignment(SwingConstants.CENTER);
		results.add(lblCount);
		results.add(lblTargetCount);

		((JLabel)results.add(new JLabel("Min"))).setHorizontalAlignment(SwingConstants.CENTER);
		results.add(lblMinDays);
		results.add(lblMinYears);

		((JLabel)results.add(new JLabel("Max"))).setHorizontalAlignment(SwingConstants.CENTER);
		results.add(lblMaxDays);
		results.add(lblMaxYears);

		((JLabel)results.add(new JLabel("Avg"))).setHorizontalAlignment(SwingConstants.CENTER);
		results.add(lblAvgDays);
		results.add(lblAvgYears);

		((JLabel)results.add(new JLabel("StdDev"))).setHorizontalAlignment(SwingConstants.CENTER);
		results.add(lblStdDevDays);
		results.add(lblStdDevYears);

		updateResults(null);
		return results;
	}

	private Warden createWarden() {
		String wardenType = modWardenParams.getSelection().getActionCommand();
		switch (wardenType) {
			case "count": {
				int prisonerCount = (int)txtPrisonerCount.getValue();
				return new Warden(prisonerCount);
			}
			case "history": {
				String[] tokens = txtPrisonerHistory.getText().split("\\s*,?\\s*");
				List<Integer> history = new ArrayList<>(tokens.length);
				for (String token : tokens) {
					try {
						history.add(Integer.parseInt(token));
					} catch (NumberFormatException ex) {
						// ignore non-numbers
					}
				}
				return new Warden(history);
			}
			default:
				throw new IllegalStateException("Unknown warden type: " + wardenType);
		}
	}

	private Protocol createProtocol(String selectedStrategyName) {
		Protocol protocol;
		if (ProtocolSingleCounter.class.getName().equals(selectedStrategyName)) {
			protocol = new ProtocolSingleCounter();
		} else if (ProtocolSCWithSmartDrones.class.getName().equals(selectedStrategyName)) {
			protocol = new ProtocolSCWithSmartDrones();
		} else if (ProtocolDynamicCounter.class.getName().equals(selectedStrategyName)) {
			protocol = new ProtocolDynamicCounter();
		} else if (ProtocolBulkWithLoop.class.getName().equals(selectedStrategyName)) {
			int stage1Length = (int)txtStage1Length.getValue();
			int stage2Length = (int)txtStage2Length.getValue();
			int bulkSize = (int)txtBulkSize.getValue();
			protocol = new ProtocolBulkWithLoop(stage1Length, stage2Length, bulkSize);
		} else if (ProtocolBulkWithRestart.class.getName().equals(selectedStrategyName)) {
			int stage1Length = (int)txtStage1Length.getValue();
			int stage2Length = (int)txtStage2Length.getValue();
			int bulkSize = (int)txtBulkSize.getValue();
			protocol = new ProtocolBulkWithRestart(stage1Length, stage2Length, bulkSize);
		} else {
			throw new IllegalArgumentException("Nem ertelmezheto protokoll: " + selectedStrategyName);
		}
		return protocol;
	}

	private void updateResults(SimulationResult result) {
		if (result != null) {
			lblCount.setText(String.valueOf(result.getCount()));
			lblMaxDays.setText(String.format("%.0f nap", result.getMaxDays()));
			lblMaxYears.setText(String.format("%.2f év", result.getMaxYears()));
			lblMinDays.setText(String.format("%.0f nap", result.getMinDays()));
			lblMinYears.setText(String.format("%.2f év", result.getMinYears()));
			lblAvgDays.setText(String.format("%.1f nap", result.getAvgDays()));
			lblAvgYears.setText(String.format("%.2f év", result.getAvgYears()));
			lblStdDevDays.setText(String.format("%.2f nap", result.getStdDevDays()));
			lblStdDevYears.setText(String.format("%.2f év", result.getStdDevYears()));
		} else {
			lblTargetCount.setText(txtIterationCount.getText());
			lblCount.setText("0");
			lblMaxDays.setText("? nap");
			lblMaxYears.setText("? év");
			lblMinDays.setText("? nap");
			lblMinYears.setText("? év");
			lblAvgDays.setText("? nap");
			lblAvgYears.setText("? év");
			lblStdDevDays.setText("? nap");
			lblStdDevYears.setText("? év");
		}
	}

	private class BackgroundSimulator extends SwingWorker<SimulationResult, SimulationResult> {
		private final Warden warden;
		private final Protocol protocol;
		private final int count;

		public BackgroundSimulator(Warden warden, Protocol protocol, int count) {
			this.warden = warden;
			this.protocol = protocol;
			this.count = count;
		}

		@Override protected SimulationResult doInBackground() throws Exception {
			SimulationResult result = new SimulationResult();
			for (int i = 0; i < count; i++) {
				if (isCancelled()) {
					break;
				}
				protocol.simulate(warden);
				result.accumulate(protocol.getDaysUntilVictory());
				publish(result);
				warden.eraseMemory();
			}
			return result;
		}

		@Override protected void process(List<SimulationResult> chunks) {
			updateResults(chunks.get(chunks.size() - 1));
		}

		@Override protected void done() {
			btnRun.setEnabled(true);
			btnStop.setEnabled(false);
			try {
				updateResults(get());
			} catch (CancellationException ex) {
				// ignore
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(SwingGUI.this, ex.toString());
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new SwingGUI();
	}
}
