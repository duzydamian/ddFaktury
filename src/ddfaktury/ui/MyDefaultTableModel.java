/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ddfaktury.ui;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author duzydamian
 */
public class MyDefaultTableModel extends DefaultTableModel{
                        public MyDefaultTableModel(Object [][] data, Object [] col) {
                                super(data,col);
                        }

                        public MyDefaultTableModel(Vector data, Vector col) {
                                super(data,col);
                        }
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
}
