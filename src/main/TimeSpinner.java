package main;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;


public class TimeSpinner extends JSpinner
{

        public TimeSpinner()
        {
                super(new SpinnerDateModel());
                JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(this, "HH:mm:ss");
                this.setEditor(timeEditor);
        }

}