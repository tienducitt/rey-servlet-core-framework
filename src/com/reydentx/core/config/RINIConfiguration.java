/*
 * Copied from org.apache.commons.configuration.INIConfiguration in apache commons-configuration-1.9 package
 */
package com.reydentx.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * <p> An initialization or ini file is a configuration file typically found on
 * Microsoft's Windows operating system and contains data for Windows based
 * applications. </p>
 *
 * <p> Although popularized by Windows, ini files can be used on any system or
 * platform due to the fact that they are merely text files that can easily be
 * parsed and modified by both humans and computers. </p>
 *
 * <p> A typical ini file could look something like: </p>
 * <pre>
 * [section1]
 * ; this is a comment!
 * var1 = foo
 * var2 = bar
 *
 * [section2]
 * var1 = doo
 * </pre>
 *
 * <p> The format of ini files is fairly straight forward and is composed of
 * three components:</p> <ul> <li><b>Sections:</b> Ini files are split into
 * sections, each section starting with a section declaration. A section
 * declaration starts with a '[' and ends with a ']'. Sections occur on one line
 * only.</li> <li><b>Parameters:</b> Items in a section are known as parameters.
 * Parameters have a typical {@code key = value} format.</li>
 * <li><b>Comments:</b> Lines starting with a ';' are assumed to be comments.
 * </li> </ul>
 *
 * <p> There are various implementations of the ini file format by various
 * vendors which has caused a number of differences to appear. As far as
 * possible this configuration tries to be lenient and support most of the
 * differences. </p>
 *
 * <p> Some of the differences supported are as follows: </p> <ul>
 * <li><b>Comments:</b> The '#' character is also accepted as a comment
 * signifier.</li> <li><b>Key value separtor:</b> The ':' character is also
 * accepted in place of '=' to separate keys and values in parameters, for
 * example {@code var1 : foo}.</li> <li><b>Duplicate sections:</b> Typically
 * duplicate sections are not allowed , this configuration does however support
 * it. In the event of a duplicate section, the two section's values are
 * merged.</li> <li><b>Duplicate parameters:</b> Typically duplicate parameters
 * are only allowed if they are in two different sections, thus they are local
 * to sections; this configuration simply merges duplicates; if a section has a
 * duplicate parameter the values are then added to the key as a list. </li>
 * </ul> <p> Global parameters are also allowed; any parameters declared
 * before a section is declared are added to a global section. It is important
 * to note that this global section does not have a name. </p> <p> In all
 * instances, a parameter's key is prepended with its section name and a '.'
 * (period). Thus a parameter named "var1" in "section1" will have the key
 * {@code section1.var1} in this configuration. Thus, a section's parameters can
 * easily be retrieved using the {@code subset} method using the section name as
 * the prefix. </p> <h3>Implementation Details:</h3> <p>Consider the following
 * ini file:</p>
 * <pre>
 *  default = ok
 *
 *  [section1]
 *  var1 = foo
 *  var2 = doodle
 *
 *  [section2]
 *  ; a comment
 *  var1 = baz
 *  var2 = shoodle
 *  bad =
 *  = worse
 *
 *  [section3]
 *  # another comment
 *  var1 : foo
 *  var2 : bar
 *  var5 : test1
 *
 *  [section3]
 *  var3 = foo
 *  var4 = bar
 *  var5 = test2
 * </pre> <p> This ini file will be parsed without error. Note:</p> <ul>
 * <li>The parameter named "default" is added to the global section, it's value
 * is accessed simply using {@code getProperty("default")}.</li> <li>Section 1's
 * parameters can be accessed using {@code getProperty("section1.var1")}.</li>
 * <li>The parameter named "bad" simply adds the parameter with an empty value.
 * </li> <li>The empty key with value "= worse" is added using an empty key.
 * This key is still added to section 2 and the value can be accessed using
 * {@code getProperty("section2.")}, notice the period '.' following the section
 * name.</li> <li>Section three uses both '=' and ':' to separate keys and
 * values.</li> <li>Section 3 has a duplicate key named "var5". The value for
 * this key is [test1, test2], and is represented as a List.</li> </ul> <p>
 * The set of sections in this configuration can be retrieved using the
 * {@code getSections} method. </p> <p> <em>Note:</em> Configuration objects of
 * this type can be read concurrently by multiple threads. However if one of
 * these threads modifies the object, synchronization has to be performed
 * manually. </p>
 *
 * @author Trevor Miller
 * @version $Id: ZINIConfiguration.java 1210003 2011-12-03 20:54:46Z oheger $
 * @since 1.4
 * @deprecated This class has been replaced by HierarchicalINIConfiguration,
 * which provides a superset of the functionality offered by this class.
 */
@Deprecated
public class RINIConfiguration extends RAbstractFileConfiguration {

	/**
	 * The characters that signal the start of a comment line.
	 */
	protected static final String COMMENT_CHARS = "#;";
	/**
	 * The characters used to separate keys from values.
	 */
	protected static final String SEPARATOR_CHARS = "=:";

	/**
	 * Create a new empty INI Configuration.
	 */
	public RINIConfiguration() {
		super();
	}

	/**
	 * Create and load the ini configuration from the given file.
	 *
	 * @param filename The name pr path of the ini file to load.
	 * @throws ConfigurationException If an error occurs while loading the file
	 */
	public RINIConfiguration(String filename) throws ConfigurationException {
		super(filename);
	}

	/**
	 * Create and load the ini configuration from the given file.
	 *
	 * @param file The ini file to load.
	 * @throws ConfigurationException If an error occurs while loading the file
	 */
	public RINIConfiguration(File file) throws ConfigurationException {
		super(file);
	}

	/**
	 * Create and load the ini configuration from the given url.
	 *
	 * @param url The url of the ini file to load.
	 * @throws ConfigurationException If an error occurs while loading the file
	 */
	public RINIConfiguration(URL url) throws ConfigurationException {
		super(url);
	}

	/**
	 * Save the configuration to the specified writer.
	 *
	 * @param writer - The writer to save the configuration to.
	 * @throws ConfigurationException If an error occurs while writing the
	 * configuration
	 */
	public void save(Writer writer) throws ConfigurationException {
		PrintWriter out = new PrintWriter(writer);
		Iterator<String> it = getSections().iterator();
		while (it.hasNext()) {
			String section = it.next();
			out.print("[");
			out.print(section);
			out.print("]");
			out.println();

			Configuration subset = subset(section);
			Iterator<String> keys = subset.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				//mod by Zing
				// vì subset (tập cùng prefix) của session vẫn có thể là session (sub-session)
				// => không thực sự là key
				// => bỏ qua
				if (key.indexOf(".") >= 0) {
					continue;
				}
				Object value = subset.getProperty(key);
				if (value instanceof Collection) {
					Iterator<?> values = ((Collection<?>) value).iterator();
					while (values.hasNext()) {
						value = values.next();
						out.print(key);
						out.print(" = ");
						out.print(formatValue(value.toString()));
						out.println();
					}
				} else {
					out.print(key);
					out.print(" = ");
					out.print(formatValue(value.toString()));
					out.println();
				}
			}

			out.println();
		}

		out.flush();
	}

	/**
	 * Load the configuration from the given reader. Note that the
	 * {@code clear()} method is not called so the configuration read in will be
	 * merged with the current configuration.
	 *
	 * @param reader The reader to read the configuration from.
	 * @throws ConfigurationException If an error occurs while reading the
	 * configuration
	 */
	public void load(Reader reader) throws ConfigurationException {
		try {
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();
			String section = "";
			while (line != null) {
				line = line.trim();
				if (!isCommentLine(line)) {
					if (isSectionLine(line)) {
						section = line.substring(1, line.length() - 1) + ".";
					} else {
						String key = "";
						String value = "";
						int index = line.indexOf("=");
						if (index >= 0) {
							key = section + line.substring(0, index);
							value = parseValue(line.substring(index + 1));
						} else {
							index = line.indexOf(":");
							if (index >= 0) {
								key = section + line.substring(0, index);
								value = parseValue(line.substring(index + 1));
							} else {
								key = section + line;
							}
						}
						addProperty(key.trim(), value);
					}
				}
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			throw new ConfigurationException("Unable to load the configuration", e);
		}
	}

	/**
	 * Parse the value to remove the quotes and ignoring the comment. Example:
	 *
	 * <pre>"value" ; comment -> value</pre>
	 *
	 * <pre>'value' ; comment -> value</pre>
	 *
	 * @param value
	 */
	private String parseValue(String value) {
		value = value.trim();

		boolean quoted = value.startsWith("\"") || value.startsWith("'");
		boolean stop = false;
		boolean escape = false;

		char quote = quoted ? value.charAt(0) : 0;

		int i = quoted ? 1 : 0;

		StringBuilder result = new StringBuilder();
		while (i < value.length() && !stop) {
			char c = value.charAt(i);

			if (quoted) {
				if ('\\' == c && !escape) {
					escape = true;
				} else if (!escape && quote == c) {
					stop = true;
				} else if (escape && quote == c) {
					escape = false;
					result.append(c);
				} else {
					if (escape) {
						escape = false;
						result.append('\\');
					}

					result.append(c);
				}
			} else {
				if (COMMENT_CHARS.indexOf(c) == -1) {
					result.append(c);
				} else {
					stop = true;
				}
			}

			i++;
		}

		String v = result.toString();
		if (!quoted) {
			v = v.trim();
		}
		return v;
	}

	/**
	 * Add quotes around the specified value if it contains a comment character.
	 */
	private String formatValue(String value) {
		boolean quoted = false;

		for (int i = 0; i < COMMENT_CHARS.length() && !quoted; i++) {
			char c = COMMENT_CHARS.charAt(i);
			if (value.indexOf(c) != -1) {
				quoted = true;
			}
		}

		if (quoted) {
			return '"' + value.replaceAll("\"", "\\\\\\\"") + '"';
		} else {
			return value;
		}
	}

	/**
	 * Determine if the given line is a comment line.
	 *
	 * @param line The line to check.
	 * @return true if the line is empty or starts with one of the comment
	 * characters
	 */
	protected boolean isCommentLine(String line) {
		if (line == null) {
			return false;
		}
		// blank lines are also treated as comment lines
		return line.length() < 1 || COMMENT_CHARS.indexOf(line.charAt(0)) >= 0;
	}

	/**
	 * Determine if the given line is a section.
	 *
	 * @param line The line to check.
	 * @return true if the line contains a secion
	 */
	protected boolean isSectionLine(String line) {
		if (line == null) {
			return false;
		}
		return line.startsWith("[") && line.endsWith("]");
	}

	/**
	 * Return a set containing the sections in this ini configuration. Note that
	 * changes to this set do not affect the configuration.
	 *
	 * @return a set containing the sections.
	 */
	public Set<String> getSections() {
		Set<String> sections = new TreeSet<String>();

		Iterator<String> keys = getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			//mod by Zing
			// vì các session được phép lồng nhau, phân cách bởi dấu chấm '.'
			// nên cần tìm '.' cuối cùng
			//int index = key.indexOf("."); //orig
			int index = key.lastIndexOf("."); //change to
			if (index >= 0) {
				sections.add(key.substring(0, index));
			}
		}

		return sections;
	}
}
