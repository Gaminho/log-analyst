package ui.last.tabs;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;

import beans.devices.Device;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.last.FileSelector;
import ui.last.components.ComponentWithImage;
import ui.last.components.ProgressPanel;
import ui.last.components.TabHeader;
import utils.Configuration;

public class FileDetailsTab extends LogTab {

	private static final long serialVersionUID = 1L;

	private static final Integer TOP = 0;
	private static final Integer FLOP = 1;

	private ProgressPanel fileInfo;
	private ProgressPanel userInfo;
	private ProgressPanel deviceInfo;
	private ProgressPanel periodInfo;
	private ProgressPanel topInfo;
	private ProgressPanel flopInfo;
	private ProgressPanel veraInfo;

	public FileDetailsTab(FileSelector pFileSelector) {
		super(pFileSelector);
		fileInfo.start();
		userInfo.start();
		deviceInfo.start();
		periodInfo.start();
		topInfo.start();
		flopInfo.start();
		veraInfo.start();
	}

	private JPanel fillGeneralFileInfo(JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(3, 2));

		// Name
		info.add(CustomComponent.boldLabel("Name"));
		info.add(new JLabel(getLogFile().getName()));
		pProgressBar.setValue(100 / 3);

		// Size
		info.add(CustomComponent.boldLabel("Size"));
		info.add(new JLabel(FileUtils.byteCountToDisplaySize(getLogFile().length())));
		pProgressBar.setValue(200 / 3);

		// Location
		info.add(CustomComponent.boldLabel("Location"));
		info.add(new JLabel(getLogFile().getPath()));
		pProgressBar.setValue(100);

		return new ComponentWithImage(Configuration.IMAGE_FILE, info);
	}

	private JPanel fillVeraInfo(JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(1, 2));
		info.add(CustomComponent.boldLabel("Vera"));
		info.add(new JLabel(getLogFile().getVeraId()));
		pProgressBar.setValue(100);

		return new ComponentWithImage(Configuration.IMAGE_VERA, info);
	}

	private JPanel fillUserInfo(JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(1, 2));
		info.add(CustomComponent.boldLabel("User"));
		info.add(new JLabel(getLogFile().getUserId()));
		pProgressBar.setValue(100);

		return new ComponentWithImage(Configuration.IMAGE_AVATAR, info);
	}

	private JPanel fillDeviceInfo(JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(getLogFile().getDeviceTypeCount() + 1, 3));
		info.add(CustomComponent.boldLabel("Device type"));
		info.add(CustomComponent.boldLabel("Device"));
		info.add(CustomComponent.boldLabel("Logs"));

		getLogFile().getDeviceTypes().forEach(type -> {
			info.add(CustomComponent.boldLabel(type.name()));
			info.add(new JLabel(String.format("%d",
					SoftLogExtractor.getDeviceIdsWithType(getLogFile().getSoftLogs(), type).size())));
			info.add(new JLabel(String.format("%d", SoftLogExtractor
					.filterByTypes(getLogFile().getSoftLogs(), Collections.singletonList(type)).size())));
			pProgressBar.setValue(pProgressBar.getValue() + (100 / getLogFile().getDeviceCount()));
		});

		return new ComponentWithImage(Configuration.IMAGE_SENSOR, info);
	}

	private JPanel fillExtremInfo(Integer pExtremum, JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(3, 3));

		File extremImage = pExtremum == TOP ? Configuration.IMAGE_WARM : Configuration.IMAGE_COLD;
		Device extremSender = pExtremum == TOP ? getLogFile().getTopSender() : getLogFile().getFlopSender();
		String extremDay = pExtremum == TOP ? getLogFile().getTopDay() : getLogFile().getFlopDay();
		String extremHour = pExtremum == TOP ? getLogFile().getTopHour() : getLogFile().getFlopHour();

		// Device
		info.add(CustomComponent.boldLabel("Device"));
		info.add(new JLabel(extremSender.getId()));
		info.add(new JLabel(String.format("%d", getLogFile().getLogByDevice(extremSender.getId()).size())));

		// Day
		info.add(CustomComponent.boldLabel("Day"));
		info.add(new JLabel(extremDay));
		info.add(new JLabel(String.format("%d", getLogFile().getLogByDay(extremDay).size())));

		// Hour
		info.add(CustomComponent.boldLabel("Hour"));
		info.add(new JLabel(extremHour));
		info.add(new JLabel(String.format("%d", getLogFile().getLogByHour(extremHour).size())));

		return new ComponentWithImage(extremImage, info);
	}

	private JPanel fillPeriodInfo(JProgressBar pProgressBar) {

		JPanel info = new JPanel(new GridLayout(3, 2));

		info.add(CustomComponent.boldLabel("Period"));
		info.add(new JLabel(getLogFile().getPeriod()));
		pProgressBar.setValue(100 / 3);

		info.add(CustomComponent.boldLabel("Day count"));
		info.add(new JLabel(String.format("%d", getLogFile().getDayCount())));
		pProgressBar.setValue(2 * 100 / 3);

		info.add(CustomComponent.boldLabel("Logs by day"));
		info.add(new JLabel(String.format("%.2f", getLogFile().getMeanLogByDay())));
		pProgressBar.setValue(100 / 3);

		return new ComponentWithImage(Configuration.IMAGE_CALENDAR, info);
	}

	@Override
	protected Component content() {
		JPanel content = new JPanel(new GridLayout(2, 1));

		// FILE
		JPanel head = new JPanel(new GridLayout(2, 1));
		head.add(fileInfo);

		// VERA AND USER
		JPanel userAndVera = new JPanel(new GridLayout(1, 2));
		userAndVera.add(userInfo);
		userAndVera.add(veraInfo);
		head.add(userAndVera);

		// HEAD
		content.add(head);

		JPanel jpanel = new JPanel(new GridLayout(2, 2));

		// BOTTOM
		jpanel.add(deviceInfo);
		jpanel.add(periodInfo);
		jpanel.add(topInfo);
		jpanel.add(flopInfo);
		content.add(jpanel);

		return content;
	}

	@Override
	protected TabHeader header() {
		return new TabHeader("General", "Please, enter a description for this tab ...", Configuration.IMAGE_SENSOR);
	}

	@Override
	protected void init() {
		super.init();

		ComponentWithImage info = new ComponentWithImage(Configuration.IMAGE_FILE,
				new JLabel("Extract file information"));
		info.setBorder(BorderFactory.createTitledBorder("File information"));
		fileInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				fileInfo.updateContent(fillGeneralFileInfo(fileInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_AVATAR, new JLabel("Extract user information"));
		info.setBorder(BorderFactory.createTitledBorder("User information"));
		userInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				userInfo.updateContent(fillUserInfo(userInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_AVATAR, new JLabel("Extract vera information"));
		info.setBorder(BorderFactory.createTitledBorder("Vera information"));
		veraInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				veraInfo.updateContent(fillVeraInfo(veraInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_SENSOR, new JLabel("Extract device information"));
		info.setBorder(BorderFactory.createTitledBorder("Device information"));
		deviceInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				deviceInfo.updateContent(fillDeviceInfo(deviceInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_CALENDAR, new JLabel("Extract period information"));
		info.setBorder(BorderFactory.createTitledBorder("Period information"));
		periodInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				periodInfo.updateContent(fillPeriodInfo(periodInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_WARM, new JLabel("Extract top information"));
		info.setBorder(BorderFactory.createTitledBorder("Tops"));
		topInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				topInfo.updateContent(fillExtremInfo(TOP, topInfo.getProgressBar()));
			}
		});

		info = new ComponentWithImage(Configuration.IMAGE_COLD, new JLabel("Extract flop information"));
		info.setBorder(BorderFactory.createTitledBorder("Flops"));
		flopInfo = new ProgressPanel(info, new Runnable() {

			@Override
			public void run() {
				flopInfo.updateContent(fillExtremInfo(FLOP, flopInfo.getProgressBar()));
			}
		});

	}

}
