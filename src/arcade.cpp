#include <fstream>
#include <string>
#include <iostream>
#include <algorithm>
#include <vector>

using namespace std;

string line;
string full_line;
string compiled_line;

vector<string> cols;
vector<vector<string>> columns;
vector<string> threads;

ofstream out("./boop.asm");

vector<string> string_split(string s, const char delimiter)
{
    size_t start = 0;
    size_t end = s.find_first_of(delimiter);
    vector<string> output;
    while (end <= string::npos)
    {
	    output.emplace_back(s.substr(start, end - start));
	    if (end == string::npos)
        {
	    	break;
        }

    	start = end + 1;
    	end = s.find_first_of(delimiter, start);
    }

    return output;
}

string trim(const string& str, const string& whitespace = " \t")
{
    const auto strBegin = str.find_first_not_of(whitespace);
    if (strBegin == string::npos)
    {
        // no content
        return "";
    }

    const auto strEnd = str.find_last_not_of(whitespace);
    const auto strRange = strEnd - strBegin + 1;
    return str.substr(strBegin, strRange);
}

void dump_columns()
{
    // iterate over every column
    for(int column = 0; column < columns.size(); column++)
    {
        // spit every line of the column
        for(int col_line = 0; col_line < columns[column].size(); col_line++)
        {
            out << columns[column][col_line] << endl;
        }

        // spit new line after column
        out << endl;
    }

    // empty columns
    columns.clear();
}

int parse(string file, string _namespace)
{
    ifstream in(file);
    while (getline(in, full_line))
    {
        // check if we have reached the end of a block
        if(full_line.find_first_not_of(' ') == string::npos)
        {
            // dump all our columns
            dump_columns();

            // proceed to next block
            continue;
        }

        // break line into columns
        cols = string_split(full_line, '\\');

        // make more column storage if we need it
        while(cols.size() > columns.size())
        {
            columns.push_back(vector<string>(0));
        }

        for(int col = 0; col < cols.size(); col++)
        {
            // reset compiled line
            compiled_line = "";

            // remove commented parts
            size_t start_of_comment = cols[col].find("#");
            cols[col] = cols[col].substr(0, start_of_comment);

            // check line is blank
            if(cols[col].find_first_not_of(' ') == string::npos)
            {
                // don't process blank lines
                continue;
            }

            // get cleaned line
            line = trim(cols[col]);

            // check for compiler directive
            if(line[0] == '@')
            {
                // get / import / include (import) directive
                if(line[1] == 'g' || line[1] == 'i')
                {
                    // TODO *NOT* declare so many variables
                    size_t file_name_start = line.find("\"") + 1;
                    size_t file_name_end = line.find_last_of("\"");
                    size_t namespace_start = line.find_last_of(" ") + 1;
                    string file_name = line.substr(file_name_start, file_name_end - file_name_start);
                    string load_namespace = line.substr(namespace_start);
                    parse(file_name, load_namespace);
                }

                // def (define) directive
                else if(line[1] == 'd')
                {
                    cout << "#WARN: Definition directive not implemented" << endl;
                }

                // eval (evaluate) directive
                else if(line[1] == 'e')
                {
                    cout << "#WARN: Evaluate directive not implemented" << endl;
                }

                // unknown directive
                else
                {
                    cout << "#WARN: Ignoring unknown directive \"" << line << "\"" << endl;
                }

                // don't add directives to the final assembly code
                continue;
            }

            // check for thread declaration
            else if(line[0] == '[')
            {
                // in between the blocks will be thread configuration params
                // but right now none exist :)
                int thread_name_start = line.find("]") + 1;
                compiled_line = trim(line.substr(thread_name_start));
            }

            // check for function declaration
            else if(line.back() == ':')
            {
                // prepend the namespace if we're in a namespace
                if(_namespace == "")
                {
                    compiled_line = line;
                }
                else
                {
                    compiled_line = _namespace + "_" + line;
                }
            }

            // just an ordinary line
            else
            {
                compiled_line = "    " + line;
            }

            // check if spitting line or caching it
            if(compiled_line.length() > 0)
            {
                if(col != 0)
                {
                    // store line in its column
                    columns[col].push_back(compiled_line);
                }
                else
                {
                    // spit line
                    out << compiled_line << endl;
                }
            }
        }
    }

    // dump any remaining columns
    dump_columns();

    in.close();
}

int main(int argc, char *argv[])
{
    out << "section	.text" << endl;
    out << "    global main" << endl << endl;
    if(argc < 2)
    {
        cout << "Usage: arcade";
    }
    else
    {
        parse(argv[1], "");
    }

    out.close();
}
