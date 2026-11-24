package com.example.elecciones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AsistenteBD extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "Elecciones.db";
    private static final int VERSION_BD = 1;

    private SQLiteDatabase bd;

    public AsistenteBD(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        bd = getWritableDatabase();
    }
    public Usuario findUsuarioById(int id) {
        String sql = "SELECT * FROM Usuarios WHERE id = ?";
        Cursor c = bd.rawQuery(sql, new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            String NIF = c.getString(c.getColumnIndexOrThrow("NIF"));
            String pwd = c.getString(c.getColumnIndexOrThrow("password"));
            boolean haVotado = c.getInt(c.getColumnIndexOrThrow("ha_votado")) == 1;
            c.close();
            return new Usuario(id, NIF, pwd, haVotado);
        }
        c.close();
        return null;
    }
    public Usuario findUsuarioByNIF(String NIF) {
        String sql = "SELECT * FROM Usuarios WHERE NIF = ?";
        Cursor c = bd.rawQuery(sql, new String[]{NIF});
        if (c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String pwd = c.getString(c.getColumnIndexOrThrow("password"));
            boolean haVotado = c.getInt(c.getColumnIndexOrThrow("ha_votado")) == 1;
            c.close();
            return new Usuario(id, NIF, pwd, haVotado);
        }
        c.close();
        return null;
    }
    public Partido findPartidoById(int idPartido) {
        String sql = "SELECT * FROM Partidos WHERE id = ?";
        Cursor c = bd.rawQuery(sql, new String[]{String.valueOf(idPartido)});
        if (c.moveToFirst()) {
            String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
            String siglas = c.getString(c.getColumnIndexOrThrow("siglas"));
            Color color = Color.values()[c.getInt(c.getColumnIndexOrThrow("color"))];
            c.close();
            return new Partido(idPartido, nombre, siglas, color);
        }
        c.close();
        return null;
    }
    public Partido findPartidoByName(String name) {
        String sql = "SELECT * FROM Partidos WHERE nombre = ?";
        Cursor c = bd.rawQuery(sql, new String[]{name.toUpperCase()});
        if (c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String siglas = c.getString(c.getColumnIndexOrThrow("siglas"));
            Color color = Color.values()[c.getInt(c.getColumnIndexOrThrow("color"))];
            c.close();
            return new Partido(id, name.toUpperCase(), siglas, color);
        }
        c.close();
        return null;
    }

    public List<Candidato> getCandidatos() {
        String sql = "SELECT * FROM Candidatos";
        Cursor c = bd.rawQuery(sql, null);
        List<Candidato> listCandidatos = new ArrayList<>(c.getCount());
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
                int idPartido = c.getInt(c.getColumnIndexOrThrow("id_partido"));
                int numVotos = c.getInt(c.getColumnIndexOrThrow("num_votos"));
                listCandidatos.add(new Candidato(id, nombre, findPartidoById(idPartido), numVotos));
            } while (c.moveToNext());
        }
        c.close();
        return listCandidatos;
    }
    public boolean actualizarUsuario(Usuario us) {
        ContentValues cv = new ContentValues();
        cv.put("NIF", us.getNIF());
        cv.put("password", us.getPassword());
        cv.put("ha_votado", us.haVotado() ? 1 : 0);
        return  bd.update("Usuarios", cv, "id = ?", new String[]{String.valueOf(us.getId())}) > 0;
    }

    public boolean actualizarCandidato(Candidato candidato) {
        ContentValues cv = new ContentValues();
        cv.put("nombre", candidato.getNombre());
        cv.put("id_partido", candidato.getPartido().getIdPartido());
        cv.put("num_votos", candidato.getNumVotos());
        return  bd.update("Candidatos", cv, "id = ?", new String[]{String.valueOf(candidato.getId())}) > 0;
    }
    public boolean actualizarCandidatos(Candidato[] candidatos) {
        boolean hasSucceded = false;
        if (candidatos == null || candidatos.length == 0) {
            return hasSucceded;
        }
        boolean inPrevTransaction = beginTransaction();
        ;
        try {
            boolean isSuccess = true;
            for (Candidato c : candidatos) {
                if (isSuccess) {
                    isSuccess = actualizarCandidato(c);
                } else {
                    break;
                }
            }
            if (isSuccess) {
                if (!inPrevTransaction) {
                    bd.setTransactionSuccessful();
                }
                hasSucceded = true;
            }
        } finally {
            if (!inPrevTransaction){
                bd.endTransaction();
            }
        }
        return hasSucceded;
    }
    public boolean beginTransaction(){
        if (!bd.inTransaction()) {
            bd.beginTransaction();
            return true;
        }
        return false;
    }
    public void endTransaction(boolean hasSucceded){
        if (hasSucceded){
            bd.setTransactionSuccessful();
        }
        bd.endTransaction();
    }

    public boolean resetAllTablas(){
        beginTransaction();
        boolean hasSucceded = false;
        try {
            String sqlDelete =
                    "DROP TABLE IF EXISTS Candidatos";
            bd.execSQL(sqlDelete);
            createTableCandidatos();
            hasSucceded = addCandidatosEjemp();
            if (hasSucceded) {
                sqlDelete =
                        "DROP TABLE IF EXISTS Partidos";
                bd.execSQL(sqlDelete);
                createTablePartidos();
                hasSucceded = addParidosEjemp();
            }
            if (hasSucceded) {
                sqlDelete =
                        "DROP TABLE IF EXISTS Usuarios";
                bd.execSQL(sqlDelete);
                createTableUsuarios();
                hasSucceded = addUsuariosEjemp();
            }
            /*if (hasSucceded) {
                sqlDelete =
                        "DROP TABLE IF EXISTS Colores";
                bd.execSQL(sqlDelete);
                createTableColores();
                hasSucceded = addColoresEjemp();
            }*/
        } catch (SQLException e) {
            hasSucceded = false;
        }
        endTransaction(hasSucceded);
        return hasSucceded;
    }
    private void createTableColores(){
        String sqlCreatePartidos =
                "CREATE TABLE IF NOT EXISTS Colores(id INTEGER PRIMARY KEY, nombre VARCHAR(50) NOT NULL UNIQUE)";
        bd.execSQL(sqlCreatePartidos);
    }
    private void createTablePartidos(){
        String sqlCreatePartidos =
                "CREATE TABLE IF NOT EXISTS Partidos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(50) NOT NULL UNIQUE, siglas VARCHAR(5) NOT NULL UNIQUE, color INTEGER NOT NULL, FOREIGN KEY(color) REFERENCES Colores(id))";
        bd.execSQL(sqlCreatePartidos);
    }

    private void createTableCandidatos(){
        String sqlCreateCandidatos =
            "CREATE TABLE Candidatos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(50) NOT NULL, id_partido INTEGER NOT NULL, num_votos INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(id_partido) REFERENCES Partidos(id))";
        bd.execSQL(sqlCreateCandidatos);
    }
    private void createTableUsuarios(){
        String sqlCreateUsuarios =
                "CREATE TABLE Usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, NIF CHAR(9) NOT NULL UNIQUE, password VARCHAR(20) NOT NULL, ha_votado INTEGER NOT NULL DEFAULT 0)";
        bd.execSQL(sqlCreateUsuarios);
    }

    private boolean addParidosEjemp(){
        boolean hasSucceded = false;
        try {
            // 3 ejemplos para la tabla Partidos
            hasSucceded = insertPartido("VOX","VOX", Color.Verde);
            if (hasSucceded) {
                hasSucceded = insertPartido("Partido Popular", "PP", Color.Azul);
            }
            if (hasSucceded) {
                hasSucceded = insertPartido("Partido Socialista Obrero Español", "PSOE", Color.Rojo);
            }
        } catch (Exception e) {
            hasSucceded = false;
        }
        return hasSucceded;
    }
    public boolean insertPartido(String nombre, String siglas, Color color){
        try {
            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre);
            cv.put("siglas", siglas);
            cv.put("color", color.ordinal());
            return bd.insert("Partidos", null, cv) > 0;
        } catch (Exception e) {
           return false;
        }
    }
    private boolean addColoresEjemp(){
        boolean hasSucceded = false;
        try {
            // 3 ejemplos para la tabla Colores
            hasSucceded = insertColor(Color.Verde);

            if (hasSucceded) {
                hasSucceded = insertColor(Color.Azul);
            }

            if (hasSucceded) {
                hasSucceded = insertColor(Color.Rojo);
            }
        } catch (Exception e) {
            hasSucceded = false;
        }
        return hasSucceded;
    }
    public boolean insertColor(Color color){
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", color.ordinal());
            cv.put("nombre", color.name());
            return bd.insert("Colores", null, cv) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean addCandidatosEjemp(){
        boolean hasSucceded = false;
        ContentValues cv = new ContentValues();

        try {
            // 6 ejemplos para la tabla Candidatos

            cv.put("nombre", "Santiago");
            cv.put("id_partido", 1);
            hasSucceded = insertCandidato("Santiago", findPartidoByName("VOX"));

            cv.clear();
            if (hasSucceded) {
                cv.put("nombre", "Pepa");
                cv.put("id_partido", 1);
                hasSucceded = bd.insert("Candidatos", null, cv) > 0;
            }

            cv.clear();
            if (hasSucceded) {
                cv.put("nombre", "Alberto");
                cv.put("id_partido", 2);
                hasSucceded = bd.insert("Candidatos", null, cv) > 0;
            }

            cv.clear();
            if (hasSucceded) {
                cv.put("nombre", "Miguel");
                cv.put("id_partido", 2);
                hasSucceded = bd.insert("Candidatos", null, cv) > 0;
            }

            cv.clear();
            if (hasSucceded) {
                cv.put("nombre", "Pedro");
                cv.put("id_partido", 3);
                hasSucceded = bd.insert("Candidatos", null, cv) > 0;
            }

            cv.clear();
            if (hasSucceded) {
                cv.put("nombre", "María");
                cv.put("id_partido", 3);
                hasSucceded = bd.insert("Candidatos", null, cv) > 0;
            }
        } catch (Exception e) {
            hasSucceded = false;
        }
        return hasSucceded;
    }
    public boolean insertCandidato(String nombre, Partido partido){
        try {
            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre);
            cv.put("id_partido", partido.getIdPartido());
            return bd.insert("Candidatos", null, cv) > 0;
            } catch (Exception e) {
            return false;
        }
    }
    private boolean addUsuariosEjemp(){
        boolean hasSucceded = false;
        ContentValues cv = new ContentValues();

        try {
            // 3 ejemplos para la tabla Usuarios
            hasSucceded = insertUsuario("11111111A", "abc123.");

            cv.clear();
            if (hasSucceded) {
                hasSucceded = insertUsuario("22222222B","abc123.");
            }

            cv.clear();
            if (hasSucceded) {
                hasSucceded = insertUsuario("33333333C", "abc123.");
            }
        } catch (Exception e) {
            hasSucceded = false;
        }
        return hasSucceded;
    }
    public boolean insertUsuario(String NIF, String password){
        try {
            ContentValues cv = new ContentValues();
            cv.put("NIF", NIF);
            cv.put("password", password);
            return bd.insert("Usuarios", null, cv) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        bd = db;
        createTableColores();
        addColoresEjemp();
        createTablePartidos();
        addParidosEjemp();
        createTableCandidatos();
        addCandidatosEjemp();
        createTableUsuarios();
        addUsuariosEjemp();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
