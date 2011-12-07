package economy.firm;

import java.awt.Color;

public enum FirmStatus {

	SUPPLYING,PRODUCING,RETAILING;


	public static Color getStatusColor(FirmStatus status){
		switch(status){

		case SUPPLYING:
			return Color.DARK_GRAY;
		case PRODUCING:
			return Color.RED;
		case RETAILING:
			return Color.BLUE;
		default:
			return Color.BLACK;
		}

	}

}
