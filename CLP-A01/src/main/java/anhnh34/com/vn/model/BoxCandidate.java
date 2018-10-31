package anhnh34.com.vn.model;

import java.nio.channels.ShutdownChannelGroupException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BoxCandidate {

	private Box box;
	private Space space;
	private int k;
	private double potenSpaceUtilization;
	private double lengthwiseProtrustion;
	private double boxVolume;

	final static Logger logger = Logger.getLogger(BoxCandidate.class);

	public int getK() {
		return k;
	}
	
	private void setLoggerLevel() {
		logger.setLevel(Level.FATAL);
	}

	public void setK(int k) {
		this.k = k;
	}

	public Space getSpace() {
		return space;
	}

	public Box getBox() {
		return box;
	}

	public double getLengthwiseProtrustion() {
		return lengthwiseProtrustion;
	}

	public double getBoxVolume() {
		return boxVolume;
	}

	public double getPotenSpaceUtilization() {
		return potenSpaceUtilization;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public BoxCandidate(Box box, Space space, int k) {
		this.k = k;
		this.box = box;
		this.space = space;
		this.setLoggerLevel();
	}

	public BoxCandidate(Box box, Space space) {
		this.setBox(box);
		this.setSpace(space);
		this.setLoggerLevel();
	}

	public void initialize(String algorithmType) {
		switch (algorithmType) {
		case Constant.GREEDY_ST:
			this.stType();
			break;
		case Constant.GREEDY_VL:
			this.vlType();				
			break;
		}
	}

	private void stType() {
		//logger.info("setup candidate");
		
		//UPRO
		// find minK		
			
			int numOfAvaiableBox = (int) Math.round(space.getHeight() / box.getSelectedRotation().getHeight());
			this.k = this.k < numOfAvaiableBox ? k : numOfAvaiableBox;
	
//			logger.info(String.format("id: %s l:%f w: %f h: %f min(%f %f %f) max(%f %f %f) %s",
//					new Object[] { box.getCustomerId(), box.getLength(), box.getWidth(), box.getHeight(),
//							box.getMinimum().getX(), box.getMinimum().getY(), box.getMinimum().getZ(),
//							box.getMaximum().getX(), box.getMaximum().getY(), box.getMaximum().getZ(),
//							box.getSelectedRotation().getRotationCode() }));
			//logger.info(String.format("avaiable number: %d, k: %d", new Object[] { numOfAvaiableBox, this.k }));
	
			// caculate pontetial Space Utilization
			this.potenSpaceUtilization = this.k * (this.box.getSelectedRotation().getLength() * this.box.getSelectedRotation().getWidth() * this.box.getSelectedRotation().getHeight()
					/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));
	
			// caculate lengthwise protrution.
			this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getSelectedRotation().getLength();
	
			// caculate volume of box.
			this.boxVolume = this.box.getSelectedRotation().getLength() * this.box.getSelectedRotation().getWidth() * this.box.getSelectedRotation().getHeight();
	
			//logger.info(String.format("%f %f %f", this.potenSpaceUtilization, this.lengthwiseProtrustion, this.boxVolume));
			//logger.info("Finish setup candidate");
		
		//End-UPRO
		
		
		// find minK		
//		int numOfAvaiableBox = (int) Math.round(space.getHeight() / box.getHeight());
//		this.k = this.k < numOfAvaiableBox ? k : numOfAvaiableBox;
//
//		logger.info(String.format("id: %s l:%f w: %f h: %f min(%f %f %f) max(%f %f %f) %s",
//				new Object[] { box.getCustomerId(), box.getLength(), box.getWidth(), box.getHeight(),
//						box.getMinimum().getX(), box.getMinimum().getY(), box.getMinimum().getZ(),
//						box.getMaximum().getX(), box.getMaximum().getY(), box.getMaximum().getZ(),
//						box.getSelectedRotation().getRotationCode() }));
//		logger.info(String.format("avaiable number: %d, k: %d", new Object[] { numOfAvaiableBox, this.k }));
//
//		// caculate pontetial Space Utilization
//		this.potenSpaceUtilization = this.k * (this.box.getLength() * this.box.getWidth() * this.box.getHeight()
//				/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));
//
//		// caculate lengthwise protrution.
//		this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getLength();
//
//		// caculate volume of box.
//		this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getHeight();
//
//		logger.info(String.format("%f %f %f", this.potenSpaceUtilization, this.lengthwiseProtrustion, this.boxVolume));
//		logger.info("Finish setup candidate");
	}

	private void vlType() {
		//UPRO		
		int feasibleBoxX = (int) Math.round(space.getLength() / this.box.getSelectedRotation().getLength());
		int feasibleBoxY = (int) Math.round(space.getWidth() / this.box.getSelectedRotation().getWidth());
		int feasibleBoxZ = (int) Math.round(space.getHeight() / this.box.getSelectedRotation().getHeight());

		int numVolumeUsageNum = feasibleBoxX * feasibleBoxY * feasibleBoxZ;

		this.k = this.k < numVolumeUsageNum ? this.k : numVolumeUsageNum;

		// caculate pontetial Space Utilization
		this.potenSpaceUtilization = this.k * (this.box.getSelectedRotation().getLength() * this.box.getSelectedRotation().getWidth() * this.box.getSelectedRotation().getHeight()
				/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));

		// caculate lengthwise protrution.
		this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getSelectedRotation().getLength();

		// caculate volume of box.
		this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getSelectedRotation().getHeight();
		//END-UPRO
		
		
		
//		int feasibleBoxX = (int) Math.round(space.getLength() / box.getLength());
//		int feasibleBoxY = (int) Math.round(space.getWidth() / box.getWidth());
//		int feasibleBoxZ = (int) Math.round(space.getHeight() / box.getHeight());
//
//		int numVolumeUsageNum = feasibleBoxX * feasibleBoxY * feasibleBoxZ;
//
//		this.k = this.k < numVolumeUsageNum ? this.k : numVolumeUsageNum;
//
//		// caculate pontetial Space Utilization
//		this.potenSpaceUtilization = this.k * (this.box.getLength() * this.box.getWidth() * this.box.getHeight()
//				/ (this.space.getLength() * this.space.getWidth() * this.space.getHeight()));
//
//		// caculate lengthwise protrution.
//		this.lengthwiseProtrustion = this.space.getMinimum().getX() + this.box.getLength();
//
//		// caculate volume of box.
//		this.boxVolume = this.box.getLength() * this.box.getWidth() * this.box.getHeight();
	}

	private void elType() {

	}

}
