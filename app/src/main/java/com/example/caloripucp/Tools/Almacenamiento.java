package com.example.caloripucp.Tools;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloripucp.Activities.InicioActivity;
import com.example.caloripucp.Beans.Catalogo;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Beans.RegistroHistorial;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Almacenamiento {


    // PERFIL INICIAL:

    public static void guardarPerfilInicial(Perfil perfil, AppCompatActivity inicioActivity){
        String archivoPerfilInicial = "perfilInicial";
        try(FileOutputStream fos = inicioActivity.openFileOutput(archivoPerfilInicial, Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(perfil);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static Perfil obtenerPerfilInicial(AppCompatActivity inicioActivity){
        String archivoPerfilInicial = "perfilInicial";
        try(FileInputStream fis = inicioActivity.openFileInput(archivoPerfilInicial); ObjectInputStream ois = new ObjectInputStream(fis)){
            return (Perfil) ois.readObject();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void eliminarPerfilInicial(AppCompatActivity inicioActivity){
        String archivoPerfilInicial = "perfilInicial";
        inicioActivity.deleteFile(archivoPerfilInicial);
    }

    // CATÁLOGO:

    public static void guardarCatalogo(Catalogo catalogo, AppCompatActivity activity){
        String archivoCatalogo = "catalogo";
        try(FileOutputStream fos = activity.openFileOutput(archivoCatalogo, Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(catalogo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static Catalogo obtenerCatalogo(AppCompatActivity activity){
        String archivoCatalogo = "catalogo";
        try(FileInputStream fis = activity.openFileInput(archivoCatalogo); ObjectInputStream ois = new ObjectInputStream(fis)){
            return (Catalogo) ois.readObject();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void eliminarCatalogo(AppCompatActivity activity){
        String archivoPerfilInicial = "catalogo";
        activity.deleteFile(archivoPerfilInicial);
    }

    // HISTORIAL:

    public static void guardarHistorial(RegistroHistorial historial, AppCompatActivity activity){
        String archivoHistorial = "historial";
        try(FileOutputStream fos = activity.openFileOutput(archivoHistorial, Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(historial);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static RegistroHistorial obtenerHistorial(AppCompatActivity activity){
        String archivoHistorial = "historial";
        try(FileInputStream fis = activity.openFileInput(archivoHistorial); ObjectInputStream ois = new ObjectInputStream(fis)){
            return (RegistroHistorial) ois.readObject();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void eliminarHistorial(AppCompatActivity activity){
        String archivoHistorial = "historial";
        activity.deleteFile(archivoHistorial);
    }

    // REGISTRO ACTUAL:

    public static void guardarRegistroDiario(Registro registro, AppCompatActivity activity){
        String archivoRegistroActual = "registroActual";
        try(FileOutputStream fos = activity.openFileOutput(archivoRegistroActual, Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static Registro obtenerRegistroDiario(AppCompatActivity activity){
        String archivoRegistroActual = "registroActual";
        try(FileInputStream fis = activity.openFileInput(archivoRegistroActual); ObjectInputStream ois = new ObjectInputStream(fis)){
            return (Registro) ois.readObject();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static void eliminarRegistroDiario(AppCompatActivity activity){
        String archivoRegistroActual = "registroActual";
        activity.deleteFile(archivoRegistroActual);
    }




}
