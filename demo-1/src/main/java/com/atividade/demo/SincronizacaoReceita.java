package com.atividade.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.opencsv.CSVWriter;

@SpringBootApplication
public class SincronizacaoReceita {
    //No exercicio foi usado duas formas para a leitura e escrita de arquivos uma utilizando o BufferedReader 
	//e outra usando o Opencsv externo que foi adicionado ao POM 
	//Não sabia qual dos formatos preferem adicionei os dois
	//java -jar demo-1-0.0.1-SNAPSHOT.jar C:\workspaces\tabelaExercicio.csv
	public static void main(String[] args) throws IOException {
		SpringApplication.run(SincronizacaoReceita.class, args);
		
		File csvFile = new File(args[0]); //cria o arquivo com o caminho passado como parametro
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		 String line = "";
		 
		 List<String[]> linhas = new ArrayList<>(); //cria as linhas que serão adicionadas ao arquivo final
		 
		 try{
			while((line = br.readLine()) != null){
				String[] count = line.split(";"); // como separador foi utilizado o ; caso este mude poderiamos adicionar validações

				 ReceitaService receitaService = new ReceitaService();

				 double saldo = Double.valueOf(count[2].replace(",","."));
				 String conta = count[1].replaceAll("[^0-9.]", "");  //o validador de conta aceita até 6 caracteres no serviço
				 
				 //Nesse exercicio não considerei realizar a valição para a primeira linha onde viriam os nomes das colunas
				 //fiz testes colocando o arquivo direto sem nomes
				 
				 if(receitaService.atualizarConta(count[0],conta,saldo,count[3])) { 
					 //chama o serviço e caso true adiciona a coluna ao final informando o sucesso no envio.
					 linhas.add(new String[]{count[0],count[1],count[2],count[3],"SUCESSO NO ENVIO"});
				 }else {
					 //caso false adiciona a mensagem de erro
					 linhas.add(new String[]{count[0],count[1],count[2],count[3],"ERRO NO ENVIO"});
				 }
			}
			//utilizando agora o opencsv criamos o novo arquivo com as linhas adicionadas
			// Como padrão o caminho onde ficará o arquivo de resultado será o mesmo onde esta o jar executado.
			Writer writer = Files.newBufferedWriter(Paths.get("./resultado"+System.currentTimeMillis()+".csv"));
			//A adição do time ao nome de arquivo foi somente para identificar mais facil durante os testes
	        CSVWriter csvWriter = new CSVWriter(writer);
	        csvWriter.writeAll(linhas);
	        csvWriter.flush();
	        writer.close();
	        
	        //Melhorias: adicionar validação do caminho, um controle de exception mais detalhado,  
	        //no ReceitaService adicionar a forma de identificar qual dos campos esta com erro e mensagem especifica para os mesmos
	        //utiliza somente uma forma para a leitura e escrita. Porem para essa atividade apenas montei a leitura e escrita.
	        //Bom dia! Ass.: joao.santana

		 }catch(FileNotFoundException | RuntimeException | InterruptedException e) {
			e.printStackTrace();
		 }		 		 
	}

}
