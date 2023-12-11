package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;
import  java.awt.*;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;

import static java.lang.System.exit;

public class Main
{
    public static void main(String[] args)
    {
        String path = "http://localhost:8080/api";

        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JTextField inputField1 = new JTextField();
        JTextField inputField2 = new JTextField();
        JTextField outputField = new JTextField();

        inputField1.setFont((new Font("Arial", Font.PLAIN, 30)));
        inputField2.setFont((new Font("Arial", Font.PLAIN, 30)));
        outputField.setFont((new Font("Arial", Font.PLAIN, 30)));

        JLabel label1 = new JLabel("Massa:");
        label1.setFont((new Font("Arial", Font.BOLD, 30)));
        JLabel label2 = new JLabel("Altura");
        label2.setFont((new Font("Arial", Font.BOLD, 30)));
        JLabel label3 = new JLabel("IMC:");
        label3.setFont((new Font("Arial", Font.BOLD, 30)));


        panel.add(label1);
        panel.add(inputField1);
        panel.add(label2);
        panel.add(inputField2);
        panel.add(label3);
        panel.add(outputField);

        String [] buttonLabels = {
                "Enviar","Sair"
        };

        for(String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 30));
            panel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try{
                        String massa, altura;

                        massa = inputField1.getText();
                        altura = inputField2.getText();

                        if(label.equals("Enviar"))
                        {
                            String content = "{\"Massa\": \" " + massa + "\", \"altura\": \" " + altura + "\"}";
                            System.out.println(content);

                            URL url = new URL(path);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type","application/json");
                            connection.setDoOutput(true);

                            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                            out.writeBytes(content);
                            out.flush();
                            out.close();
                            int responseCode = connection.getResponseCode();
                            System.out.println("Código de Resposta: " + responseCode);
                            if(responseCode != HttpURLConnection.HTTP_OK){
                                System.out.println("Got an unexpected response code");
                            }
                            BufferedReader reader = new BufferedReader(new
                                    InputStreamReader(connection.getInputStream()));
                            String linha;

                            while ((linha = reader.readLine()) != null) {
                                System.out.println(linha);
                                JsonElement jsonElement = JsonParser.parseString(linha);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                outputField.setText(jsonObject.get("IMC").getAsString());
                            }
                            reader.close();
                        }
                        if(label.equals("Sair"))
                        {
                            System.exit(0);
                        }
                    }
                    catch (MalformedURLException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch(NumberFormatException ex){
                        outputField.setText("Erro: Entrada invalida");
                    }
                }
            });
        }
        frame.add(panel);
        frame.setVisible(true);
    }
}