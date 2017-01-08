package org.neocities.autoart.autoart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Stack;

public class AutoArt extends AppCompatActivity
{

    int sgn(double x)
    {
        if (x > 0)
            return 1;
        return -1;
    }

    double mod(double a, double b)
    {
        if (b <= 0)
            throw new RuntimeException("Mod <=0! Mod < 0 not implemented yet!");

        if (sgn(a) == sgn(b))
        {
            return a % b;
        }
        else
        {
            while (a < 0)
            {
                a += b;
            }
            return a;
        }
    }


    final int FUNCTION_LENGTH = 20;
    double[][] matrix_add(double[][] a, double[][] b)
    {
        double[][] c = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
                c[i][j] = a[i][j] + b[i][j];
        }
        return c;
    }

    double[][] matrix_sub(double[][] a, double[][] b)
    {
        double[][] c = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
                c[i][j] = a[i][j] - b[i][j];
        }
        return c;
    }

    double[][] matrix_mul(double[][] a, double[][] b)
    {
        double[][] c = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
                c[i][j] = a[i][j] * b[i][j];
        }
        return c;
    }

    double[][] matrix_add_constant(double[][] m, double c)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
        {
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = m[i][j] + c;
        }
        return cpy;
    }

    double[][] matrix_scale(double[][] m, double c)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
        {
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = m[i][j] * c;
        }
        return cpy;
    }

    double[][] sin_matrix(double[][] m)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = (int)(255*Math.sin((double)m[i][j]));
        return cpy;
    }

    double[][] cos_matrix(double[][] m)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = (int)(255*Math.cos((double) m[i][j]));
        return cpy;
    }

    double[][] sqrt_matrix(double[][] m)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = (int)(255*Math.sqrt(Math.abs((double) m[i][j])));
        return cpy;
    }

    double[][] matrix_mod256(double[][] m)
    {
        double[][] cpy = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
        {
            for (int j = 0; j < m[i].length; j++)
                cpy[i][j] = mod(m[i][j], 256);
        }
        return cpy;
    }

    double[][] twod_array_clone(double[][] a)
    {
        double[][] cpy = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            cpy[i] = a[i].clone();
        return cpy;
    }

    double[][] normalize(double[][] a)
    {
        double sum = 0;
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                sum += a[i][j];
            }
        }
        double mean = sum / (a.length * a[0].length);
        double sumSqDiff = 0;
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                sumSqDiff += (a[i][j]-mean)*(a[i][j]-mean);
            }
        }
        double sigma = Math.sqrt(sumSqDiff/(a.length*a[0].length));
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                a[i][j] = 255*(a[i][j]-mean)/sigma;
            }
        }

        return a;
    }

    int randrange(int start, int end)
    {
        return (int)(Math.random()*(end-start)+start);
    }

    String randFunction()
    {
        String function = null;
        boolean hasX = false;
        boolean hasY = false;
        int operation;
        int numbersOnStack;
        int i;
        while (!(hasX && hasY))
        {
            function = "x ";
            hasX = false;
            hasY = false;
            numbersOnStack = 1;
            for (i = 0; i < FUNCTION_LENGTH; i++)
            {
                if (numbersOnStack > 1)
                    operation = randrange(0, 9);
                else
                    operation = randrange(0, 6);
                switch (operation)
                {
                    case 0:
                        function += "x ";
                        hasX = true;
                        numbersOnStack++;
                        break;
                    case 1:
                        function += "y ";
                        hasY = true;
                        numbersOnStack++;
                        break;
                    case 2:
                        function += String.valueOf(randrange(0, 200)) + " ";
                        numbersOnStack++;
                        break;
                    case 3:
                        function += "sqrt ";
                        break;
                    case 4:
                        function += "cos ";
                        break;
                    case 5:
                        function += "sin ";
                        break;
                    case 6:
                        function += "+ ";
                        numbersOnStack--;
                        break;
                    case 7:
                        function += "- ";
                        numbersOnStack--;
                        break;
                    case 8:
                        function += "* ";
                        numbersOnStack--;
                        break;

                }
            }
            while (numbersOnStack-- > 1)
            {
                operation = randrange(0, 3);
                switch(operation)
                {
                    case 0:
                        function += "+ ";
                        break;
                    case 1:
                        function += "- ";
                        break;
                    case 2:
                        function += "* ";
                        break;
                }
            }

        }
        return function;

    }

    protected double[][] evalFunction(String function, int width, int height)
    {
        Stack<double[][]> stack = new Stack<>();
        double[][] x = new double[width][height];
        double[][] y = new double[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                x[i][j] = i;
                y[i][j] = j;
            }
        }


        String[] tokens = function.split(" ");

        for (String token: tokens) {
            if (token.length() == 0)
                continue;

            if (token.equals("x"))
                stack.push(twod_array_clone(x));
            else if (token.equals("y"))
                stack.push(twod_array_clone(y));
            else if (token.equals("sin"))
                stack.push(sin_matrix(twod_array_clone(stack.pop())));
            else if (token.equals("cos"))
                stack.push(cos_matrix(twod_array_clone(stack.pop())));
            else if (token.equals("sqrt"))
                stack.push(sqrt_matrix(twod_array_clone(stack.pop())));
            else if (token.equals("+"))
            {
                double[][] a = (stack.pop());
                double[][] b = (stack.pop());
                if (a.length == 1 && a[0].length == 1 && !(b.length == 1 && b[0].length == 1))
                    stack.push(matrix_add_constant(b, a[0][0]));
                else if (!(a.length == 1 && a[0].length == 1) && b.length == 1 && b[0].length == 1)
                    stack.push(matrix_add_constant(a, b[0][0]));
                else
                    stack.push(matrix_add(a, b));
                 
            }
            else if (token.equals("-"))
            {
                double[][] a = (stack.pop());
                double[][] b = (stack.pop());
                if (a.length == 1 && a[0].length == 1 && !(b.length == 1 && b[0].length == 1))
                    stack.push(matrix_add_constant(b, -a[0][0]));

                else if (!(a.length == 1 && a[0].length == 1) && b.length == 1 && b[0].length == 1)
                    stack.push(matrix_add_constant(a, -b[0][0]));

                else
                    stack.push(matrix_sub(a, b));
            }
            else if (token.equals("*"))
            {
                double[][] a = (stack.pop());
                double[][] b = (stack.pop());
                if (a.length == 1 && a[0].length == 1 && !(b.length == 1 && b[0].length == 1))
                    stack.push(matrix_scale(b, a[0][0]));

                else if (!(a.length == 1 && a[0].length == 1) && b.length == 1 && b[0].length == 1)
                    stack.push(matrix_scale(a, b[0][0]));

                else
                    stack.push(matrix_mul(a, b));

            }
            else
            {
                double[][] m = new double[1][1];
                m[0][0] = Integer.parseInt(token);
                stack.push(m);
            }

        }
        return stack.pop();
    }

    protected void on_button_press()
    {
        EditText widthEdit = (EditText)findViewById(R.id.image_width_edit);
        EditText heightEdit = (EditText)findViewById(R.id.image_height_edit);
        int width = Integer.parseInt(widthEdit.getText().toString());
        int height = Integer.parseInt(heightEdit.getText().toString());
        double[][] r = matrix_mod256(normalize(evalFunction(randFunction(), width, height)));
        double[][] g = matrix_mod256(normalize(evalFunction(randFunction(), width, height)));
        double[][] b = matrix_mod256(normalize(evalFunction(randFunction(), width, height)));
        final Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        double gr, bl, rd;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                rd = r[i][j];
                gr = g[i][j];
                bl = b[i][j];
                img.setPixel(i, j, Color.argb(255, (int)rd, (int)gr, (int)bl));

            }

        }
        ImageView imgv = (ImageView) findViewById(R.id.image_view);
        imgv.setImageBitmap(img);
        imgv.setOnClickListener(new View.OnClickListener()
        {
           public void onClick(View v)
           {
               File root = Environment.getExternalStorageDirectory();
               String filename = ""+Math.random();
               filename = filename.substring(2);
               File imgFile = new File(root.getAbsolutePath() + "/DCIM/Camera/" + filename  + ".png");
               try
               {
                   imgFile.createNewFile();
                   FileOutputStream ostream = new FileOutputStream(imgFile);
                   img.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                   ostream.close();
               }
               catch(Exception e)
               {
                   e.printStackTrace();
               }
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutoArt.this);
               alertDialogBuilder.setMessage("The image has been downloaded");
               alertDialogBuilder.setPositiveButton("Ok",
               new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface arg0, int arg1) {
                   }
               });

               AlertDialog alertDialog = alertDialogBuilder.create();
               alertDialog.show();
           }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_art);

        final Button create_button = (Button)findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                on_button_press();
            }
        });
    }

}
