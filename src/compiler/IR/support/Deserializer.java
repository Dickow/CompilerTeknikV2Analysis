package compiler.IR.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import compiler.Exceptions.XmlParseException;
import compiler.IR.IR;

public class Deserializer {

	// For reading
	private static int linecounter = 0;
	private static Map<Integer, Object> deserializedObjectsMap;
	private static Map<Integer, List<ObjectFieldContainer>> objectsBeingDeserializedMap;
	
	private static int collectionindex = 0;

	public IR deserialize(String filename) throws FileNotFoundException {
		deserializedObjectsMap = new HashMap<Integer, Object>();
		objectsBeingDeserializedMap = new HashMap<Integer, List<ObjectFieldContainer>>();
		FileInputStream fstream = new FileInputStream(filename);
		Scanner scanner = new Scanner(fstream);
		try {
			IR loadedIR = (IR) readXml(scanner, getNextLine(scanner), null, null);
			return loadedIR;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Object readXml(Scanner scanner, String firstline, Field field, Object instance)
			throws Exception {

		Object result = null;
		String tag = firstline;
		if (tag.charAt(0) == '<' && tag.charAt(tag.length() - 1) == '>') { // Validation
																			// of
																			// XML-tag
			String innerTagName = tag.substring(1, tag.length() - 1);
			String[] attributes = innerTagName.split(" ");
			String classname = attributes[0];
			
			System.out.println(innerTagName + "@" + classname);

			int id = -1;
			if (attributes.length > 1) {
				id = Integer.parseInt(attributes[1].substring(3));
			}
			if (objectsBeingDeserializedMap.containsKey(id)) {
				ObjectFieldContainer ofcontainer = new ObjectFieldContainer(field, instance);
				if (ofcontainer.type == ObjectFieldContainer.COLLECTION) {
					ofcontainer.collectionindex = collectionindex;
				}
				objectsBeingDeserializedMap.get(id).add(ofcontainer);
				if (!getNextLine(scanner).equals(
						SerializationUtilities.IS_ALREADY_SERIALIZED)) {
					throw new XmlParseException("Expected "
							+ SerializationUtilities.IS_ALREADY_SERIALIZED,
							linecounter);
				} else if (!isMatchingClosingTag(getNextLine(scanner),
						classname)) {
					throw new XmlParseException("Expected type closing tag",
							linecounter);
				}
			} else if (deserializedObjectsMap.containsKey(id)) {
				if (!getNextLine(scanner).equals(
						SerializationUtilities.IS_ALREADY_SERIALIZED)) {
					throw new XmlParseException("Expected "
							+ SerializationUtilities.IS_ALREADY_SERIALIZED,
							linecounter);
				} else if (!isMatchingClosingTag(getNextLine(scanner),
						classname)) {
					throw new XmlParseException("Expected type closing tag",
							linecounter);
				}
				result = deserializedObjectsMap.get(id);

			} else {
				if (id >= 0) {
					objectsBeingDeserializedMap.put(id,
							new ArrayList<ObjectFieldContainer>());
				}
				Class<?> tagclass = Class.forName(classname);
				if (Map.class.isAssignableFrom(tagclass)) {
					result = readMap(scanner, classname, tagclass);
				} else if (Collection.class.isAssignableFrom(tagclass)) {
					result = readCollection(scanner, classname, tagclass);
				} else if (tagclass.isEnum()) {
					result = readEnum(scanner, result, classname, tagclass);
				} else if (SerializationUtilities.isPrimitiveType(tagclass)) {
					result = readPrimitive(scanner, classname, tagclass);
				} else {
					result = readObject(scanner, classname, tagclass);
				}
				if (result != null) {
					List<ObjectFieldContainer> containerlist = objectsBeingDeserializedMap.get(id);
					for (ObjectFieldContainer c : containerlist) {
						if (c.type == ObjectFieldContainer.FIELD) {
							c.field.set(c.object, result);
						} else if (c.type == ObjectFieldContainer.COLLECTION) {
							if (c.object instanceof List) {
								List<Object> list = (List<Object>) c.object;
								list.add(c.collectionindex, result);
							} else {
								Collection<Object> coll = (Collection<Object>) c.object;
								coll.add(c.object);
							}
						}
					}
					objectsBeingDeserializedMap.remove(id);
					deserializedObjectsMap.put(id, result);
				}
			}
		}
		return result;
	}

	private Object readEnum(Scanner scanner, Object result,
			String classname, Class<?> tagclass) throws XmlParseException {
		Object[] constants = tagclass.getEnumConstants();
		String enumvalue = getNextLine(scanner);
		for (Object c : constants) {
			if (c.toString().equals(enumvalue)) {
				result = c;
				break;
			}
		}
		if (result == null) {
			throw new XmlParseException("Unable to parse enum", linecounter);
		}
		if (!isMatchingClosingTag(getNextLine(scanner), classname)) {
			throw new XmlParseException("Excepted enumtype closing tag",
					linecounter);
		}
		return result;
	}

	private Object readPrimitive(Scanner scanner, String classname,
			Class<?> tagclass) throws XmlParseException {
		String value = getNextLine(scanner);
		Object result = null;
		if (Integer.class.isAssignableFrom(tagclass)) {
			result = new Integer(value);
		} else if (Boolean.class.isAssignableFrom(tagclass)) {
			result = new Boolean(value);
		} else if (Character.class.isAssignableFrom(tagclass)) {
			result = new Character(value.charAt(0));
		} else if (String.class.isAssignableFrom(tagclass)) {
			if (value.equals(SerializationUtilities.NULLREF)) {
				result = null;
			} else {
				result = value;
			}
		} else {
			throw new XmlParseException(
					"Unknown object encountered during parsing", linecounter);
		}
		if (!isMatchingClosingTag(getNextLine(scanner), classname)) {
			throw new XmlParseException("Expected type closing tag",
					linecounter);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object readMap(Scanner scanner, String classname,
			Class<?> tagclass) throws Exception {
		Object result;
		String datatag = getNextLine(scanner);
		if (datatag.equals(SerializationUtilities.NULLREF)) {
			result = validateAndReturnNull(scanner, classname);
		} else {
			Map<Object, Object> minstance = (Map<Object, Object>) tagclass
					.newInstance();
			while (!isMatchingClosingTag(datatag, classname)) {
				if (!datatag.equals("<KeyValuePair>")) {
					throw new XmlParseException("Expected KeyValuePair tag",
							linecounter);
				}
				if (!getNextLine(scanner).equals("<key>")) {
					throw new XmlParseException("Expected key tag", linecounter);
				}
				Object key = readXml(scanner, getNextLine(scanner), null, minstance);
				if (!isMatchingClosingTag(getNextLine(scanner), "key")) {
					throw new XmlParseException("Expected key closing tag.",
							linecounter);
				}
				if (!getNextLine(scanner).equals("<value>")) {
					throw new XmlParseException("Expected value tag",
							linecounter);
				}
				Object value = readXml(scanner, getNextLine(scanner), null, minstance);
				if (!isMatchingClosingTag(getNextLine(scanner), "value")) {
					throw new XmlParseException("Expected value closing tag.",
							linecounter);
				}
				if (!isMatchingClosingTag(getNextLine(scanner), "KeyValuePair")) {
					throw new XmlParseException(
							"Expected KeyValuePair closing tag.", linecounter);
				}
				minstance.put(key, value);
				datatag = getNextLine(scanner);
			}
			result = minstance;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object readCollection(Scanner scanner, String classname,
			Class<?> tagclass) throws Exception {
		Object result;
		String datatag = getNextLine(scanner);
		if (datatag.equals(SerializationUtilities.NULLREF)) {
			result = validateAndReturnNull(scanner, classname);
		} else {
			Collection<Object> cinstance = (Collection<Object>) tagclass
					.newInstance();
			int counter = 0;
			while (!isMatchingClosingTag(datatag, classname)) {
				collectionindex = counter;
				Object dataobject = readXml(scanner, datatag, null, cinstance);
				if (dataobject != null) {
					cinstance.add(dataobject);
				}
				datatag = getNextLine(scanner);
				counter++;
			}
			collectionindex = 0;
			result = cinstance;
		}
		return result;
	}

	private Object validateAndReturnNull(Scanner scanner,
			String classname) throws XmlParseException {
		if (!isMatchingClosingTag(getNextLine(scanner), classname)) {
			throw new XmlParseException("Expected type closing tag",
					linecounter);
		}
		return null;
	}

	private Object readObject(Scanner scanner, String classname,
			Class<?> tagclass) throws Exception {
		Object result;
		// Fill out fields of the object

		String fieldtag = getNextLine(scanner);

		// If object was not initialized when being serialized
		if (fieldtag.equals(SerializationUtilities.NULLREF)) {
			result = validateAndReturnNull(scanner, classname);
		} else {
			result = readFields(scanner, classname, tagclass, fieldtag);
		}
		return result;
	}

	private Object readFields(Scanner scanner, String classname,
			Class<?> tagclass, String fieldtag) throws Exception {
		Object result;
		Object instance = tagclass.newInstance();
		List<Field> tagclassfields = SerializationUtilities
				.getAllFields(tagclass);
		// Reading fields until we hit the matching closing tag.
		while (!isMatchingClosingTag(fieldtag, classname)) {
			String fieldname = fieldtag.substring(1, fieldtag.length() - 1);
			Field field = getFieldFromList(fieldname, tagclassfields);
			if (field == null) {
				throw new XmlParseException("No field \"" + fieldname
						+ "\" in class \"" + classname + "\"", linecounter);
			}
			field.setAccessible(true);
			Object fieldvalue = readXml(scanner, getNextLine(scanner), field, instance);
			if (!isMatchingClosingTag(getNextLine(scanner), fieldname)) {
				throw new XmlParseException("Expected field closing tag.",
						linecounter);
			}
			field.set(instance, fieldvalue);

			fieldtag = getNextLine(scanner);
		}
		result = instance;
		return result;
	}

	private Field getFieldFromList(String fieldname,
			List<Field> fieldlist) {
		for (Field f : fieldlist) {
			if (f.getName().equals(fieldname))
				return f;
		}
		return null;
	}

	private boolean isMatchingClosingTag(String tag, String classname)
			throws XmlParseException {
		if (tag.charAt(0) == '<' && tag.charAt(tag.length() - 1) == '>') { // Validation
																			// of
																			// XML-tag
			return tag.charAt(1) == '/' && tag.indexOf(classname) == 2;
		} else {
			throw new XmlParseException("Could not read XML-tag", linecounter);
		}
	}

	private String getNextLine(Scanner scanner) {
		linecounter++;
		return scanner.nextLine().trim();
	}

	public class ObjectFieldContainer {
		public int type;
		public final static int FIELD = 0;
		public final static int COLLECTION = 1;
		public final static int MAP = 2;
		
		public Field field;
		public Object object;
		public int collectionindex;
		
		public ObjectFieldContainer(Field field, Object object) {
			this.field = field;
			this.object = object;
			if (field == null) {
				type = object instanceof Collection ? COLLECTION : MAP;
			} else {
				type = FIELD;
			}
		}
	}
}
