package com.ac.ict.nlp.main;

import com.ac.ict.nlp.cws.CWS;
import com.ac.ict.nlp.pos_tagging.POS_Tagging;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author wanghongyang
 */
public class CWS_and_POS_Tagging extends javax.swing.JFrame {

    File file = null;
    String filename;
    CWS cws = new CWS();
    POS_Tagging pos = new POS_Tagging();
    String POSResult;
    private Frame f;
    private FileDialog fd;

    /*
     * Creates new form CWS_and_POS_Tagging
     */
    public CWS_and_POS_Tagging() {
        //初始化GUI组件
        initComponents();
        
        //加载分词词典并做分词预处理
        System.out.println("loading dictionary...");
        cws.loadDic("/library.txt");

        // 载入词性标注语料库并做相应的词性标注预处理
        System.out.println("loading corpus...");
        pos.readCorpus("/ChineseCorpus199801.txt");
        pos.characterSum();
        pos.phraseSum();
        pos.transformFrequencySum();
        pos.emissonFrequencySum();
        pos.calculatePrioriProbability();
        pos.calculateTransformProbability();
        pos.calculateEmissionProbability();
    }

    @SuppressWarnings("unchecked")
    /*
     * 初始化GUI组件
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        exitButton = new javax.swing.JButton();
        CWSButton = new javax.swing.JButton();
        POS_taggingButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        browseTextAera = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultTextAera = new javax.swing.JTextArea();
        menuBar1 = new javax.swing.JMenuBar();
        fileMenu1 = new javax.swing.JMenu();
        openMenuItem1 = new javax.swing.JMenuItem();
        exitMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("操作选项"));

        exitButton.setText("退出程序");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        CWSButton.setText("中文分词");
        CWSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CWSButtonActionPerformed(evt);
            }
        });

        POS_taggingButton.setText("词性标注");
        POS_taggingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                POS_taggingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CWSButton)
                .addGap(156, 156, 156)
                .addComponent(POS_taggingButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                .addComponent(exitButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exitButton)
                    .addComponent(CWSButton)
                    .addComponent(POS_taggingButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("文件预览区"));

        browseTextAera.setColumns(20);
        browseTextAera.setRows(5);
        jScrollPane1.setViewportView(browseTextAera);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("结果展示区"));

        resultTextAera.setColumns(20);
        resultTextAera.setRows(5);
        jScrollPane2.setViewportView(resultTextAera);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        fileMenu1.setText("文件");

        openMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem1.setText("打开文件");
        openMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(openMenuItem1);

        exitMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem1.setText("退出程序");
        exitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(exitMenuItem1);

        menuBar1.add(fileMenu1);

        setJMenuBar(menuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

     /*
     * 退出程序按钮
     */
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        System.exit(0);
    }                                          

     /*
     * 词性标注按钮
     */
    private void POS_taggingButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        resultTextAera.setText("");		
        String lineString;
        StringBuffer outcontent = new StringBuffer();
        System.out.println("Starting to CWS...");
        try {
            BufferedReader bfl = new BufferedReader(new FileReader(file.getAbsolutePath()));
            while ((lineString = bfl.readLine()) != null) {
                System.out.println(lineString.trim());
                ArrayList<String> segment = cws.segmentation(lineString.trim());
                ArrayList<String> smallArrayList = pos.smallSeg(segment);
                String[] example = new String[smallArrayList.size()];
                for (int i = 0; i < example.length; i++) {
                    example[i] = smallArrayList.get(i);
                }
                System.out.println("Starting to POS_tagging...");
                pos.viterbi(example);
                for (int i = 0; i <= example.length - 1; i++) {
                    System.out.print(example[i] + " ");
                    outcontent.append(example[i]);
                }
                System.out.println();
                outcontent.append("\n");
            }
            POSResult = outcontent.toString();
            resultTextAera.append(POSResult);
            bfl.close();
        } catch (IOException ero) {
            ero.printStackTrace();
        }
        System.out.println("POS_tagging finished...");
    }                                                 

     /*
     * 菜单栏打开文件按钮
     */
    private void openMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            try {
                browseTextAera.read(new FileReader(file.getAbsolutePath()), null);
            } catch (IOException ex) {
                System.out.println("problem accessing file" + file.getAbsolutePath());
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }                                             

     /*
     * 菜单栏退出程序按钮
     */
    private void exitMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {                                              
        System.exit(0);
    }                                             

     /*
     * 中文分词按钮
     */
    private void CWSButtonActionPerformed(java.awt.event.ActionEvent evt) {                                          
        resultTextAera.setText("");
        String lineString;
        StringBuffer outcontent = new StringBuffer();
        System.out.println("Starting to CWS...");
        try {
            BufferedReader bfl = new BufferedReader(new FileReader(file.getAbsolutePath()));
            while ((lineString = bfl.readLine()) != null) {
                System.out.println(lineString.trim());
                ArrayList<String> segment = cws.segmentation(lineString.trim());
                String[] example = new String[segment.size()];
                for (int i = 0; i < example.length; i++) {
                    example[i] = segment.get(i);
                }
                for (int i = 0; i <= example.length - 1; i++) {
                    System.out.print(example[i] + " ");
                    outcontent.append(example[i] + " ");
                }
                System.out.println();
                outcontent.append("\n");
            }
            POSResult = outcontent.toString();
            resultTextAera.append(POSResult);
            bfl.close();
        } catch (IOException ero) {
            ero.printStackTrace();
        }
        System.out.println("CWS finished...");
    }                                         

    /*
     * 程序主函数
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CWS_and_POS_Tagging.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CWS_and_POS_Tagging.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CWS_and_POS_Tagging.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CWS_and_POS_Tagging.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CWS_and_POS_Tagging().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton CWSButton;
    private javax.swing.JButton POS_taggingButton;
    private javax.swing.JTextArea browseTextAera;
    private javax.swing.JButton exitButton;
    private javax.swing.JMenuItem exitMenuItem1;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JMenuItem openMenuItem1;
    private javax.swing.JTextArea resultTextAera;
    // End of variables declaration                   
}
