package com.andrelourdes.group2;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledTasks {
     public static void main(String[] args) throws InterruptedException{
         try (ScheduledExecutorService schedule = Executors.newScheduledThreadPool(2)){

             // Tarefa 1: Executar uma vez após um atraso de 3 segundos.
                schedule.schedule(() -> {
                    System.out.println("Tarefa de execução única executada às" + LocalDateTime.now());
                }, 3, java.util.concurrent.TimeUnit.SECONDS);

             // Tarefa 2: Executar a cada 5 segundos, com um atraso inicial de 1 segundo.
             // scheduleAtFixedRate inicia a próxima execução no tempo previsto,
             // mesmo que a execução anterior tenha atrasado.

                schedule.scheduleAtFixedRate(() -> {
                    System.out.println("Tarefa de execução periódica executada às " + LocalDateTime.now());
                    try{
                        Thread.sleep(1000);// Simula trabalho.
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                    }
                }, 1, 5, java.util.concurrent.TimeUnit.SECONDS);

             // Tarefa 3: Executar com um atraso fixo de 5 segundos entre o fim de uma
             // execução e o início da próxima.
                schedule.scheduleWithFixedDelay(() -> {
                    System.out.println("Tarefa periódica (fixed delay) executada às  " + LocalDateTime.now());
                    try{
                        Thread.sleep(2000);// Simula trabalho.
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, 1, 5, java.util.concurrent.TimeUnit.SECONDS);

             // Deixa o scheduler rodar por 20 segundos para observação.
             Thread.sleep(20000);
         } // shutdown() é chamado aqui.
     }
}
