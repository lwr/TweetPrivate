/**
 * Copyright (c) 2006 Mailtech.cn, Ltd. All Rights Reserved.
 */

package solocompany.commons.collections;

import java.io.*;

/**
 * Abstract test class for {@link Object} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject()} method.
 * <p>
 * If your {@link Object} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Object} fails.
 *
 * @version $Revision: 1.7 $ $Date: 2004/06/01 22:55:14 $
 *
 * @author Rodney Waldhoff
 * @author Stephen Colebourne
 * @author Anonymous
 */
@SuppressWarnings("ALL")
public abstract class AbstractTestObject extends BulkTest {


    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractTestObject(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return the object to test.
     *
     * @return the object to test
     */
    public abstract Object makeObject();

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize an "empty" Collection.
     * (e.g. Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsEmptyCollections() {
        return true;
    }

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize a "full" Collection.
     * (e.g. Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsFullCollections() {
        return true;
    }

    /**
     * Is serialization testing supported.
     * Default is true.
     */
    public boolean isTestSerialization() {
        return true;
    }

    /**
     * Returns true to indicate that the collection supports equals() comparisons.
     * This implementation returns true;
     */
    public boolean isEqualsCheckable() {
        return true;
    }

    /**
     * Returns true to indicate that the collection supports fail fast iterators.
     * The default implementation returns true;
     */
    public boolean failFastIsSupported = true;
    public boolean isFailFastSupported() {
        return failFastIsSupported;
    }


    //-----------------------------------------------------------------------
    public void testObjectEqualsSelf() {
        Object obj = makeObject();
        assertEquals("A Object should equal itself", obj, obj);
    }

    public void testEqualsNull() {
        Object obj = makeObject();
        //noinspection ObjectEqualsNull
        assertFalse(obj.equals(null)); // make sure this doesn't throw NPE either
    }

    public void testObjectHashCodeEqualsSelfHashCode() {
        Object obj = makeObject();
        assertEquals("hashCode should be repeatable", obj.hashCode(), obj.hashCode());
    }

    public void testObjectHashCodeEqualsContract() {
        Object obj1 = makeObject();
        if (obj1.equals(obj1)) {
            assertEquals(
                "[1] When two objects are equal, their hashCodes should be also.",
                obj1.hashCode(), obj1.hashCode());
        }
        Object obj2 = makeObject();
        if (obj1.equals(obj2)) {
            assertEquals(
                "[2] When two objects are equal, their hashCodes should be also.",
                obj1.hashCode(), obj2.hashCode());
            assertTrue(
                "When obj1.equals(obj2) is true, then obj2.equals(obj1) should also be true",
                obj2.equals(obj1));
        }
    }

    public void testSerializeDeserializeThenCompare() throws Exception {
        Object obj = makeObject();
        if (obj instanceof Serializable && isTestSerialization()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj))", obj, dest);
            }
        }
    }

    /**
     * Sanity check method, makes sure that any Serializable
     * class can be serialized and de-serialized in memory,
     * using the handy makeObject() method
     */
    public void testSimpleSerialization() throws Exception {
        Object o = makeObject();
        if (o instanceof Serializable && isTestSerialization()) {
            byte[] objekt = writeExternalFormToBytes((Serializable) o);
            readExternalFormFromBytes(objekt);
        }
    }

    /**
     * Tests serialization by comparing against a previously stored version in CVS.
     * If the test object is serializable, confirm that a canonical form exists.
     */
    public void testCanonicalEmptyCollectionExists() {
        if (supportsEmptyCollections() && isTestSerialization() && !skipSerializedCanonicalTests()) {
            Object object = makeObject();
            if (object instanceof Serializable) {
                String name = getCanonicalEmptyCollectionName(object);
                assertTrue(
                    "Canonical empty collection (" + name + ") is not in CVS",
                    new File(name).exists());
            }
        }
    }

    /**
     * Tests serialization by comparing against a previously stored version in CVS.
     * If the test object is serializable, confirm that a canonical form exists.
     */
    public void testCanonicalFullCollectionExists() {
        if (supportsFullCollections() && isTestSerialization() && !skipSerializedCanonicalTests()) {
            Object object = makeObject();
            if (object instanceof Serializable) {
                String name = getCanonicalFullCollectionName(object);
                assertTrue(
                    "Canonical full collection (" + name + ") is not in CVS",
                    new File(name).exists());
            }
        }
    }

    // protected implementation
    //-----------------------------------------------------------------------
    /**
     * Get the version of Collections that this object tries to
     * maintain serialization compatibility with. Defaults to 1, the
     * earliest Collections version. (Note: some collections did not
     * even exist in this version).
     *
     * This constant makes it possible for TestMap (and other subclasses,
     * if necessary) to automatically check CVS for a versionX copy of a
     * Serialized object, so we can make sure that compatibility is maintained.
     * See, for example, TestMap.getCanonicalFullMapName(Map map).
     * Subclasses can override this variable, indicating compatibility
     * with earlier Collections versions.
     *
     * @return The version, or <code>null</code> if this object shouldn't be
     * tested for compatibility with previous versions.
     */
    public String getCompatibilityVersion() {
        return "1";
    }

    protected String getCanonicalEmptyCollectionName(Object object) {
        StringBuffer retval = new StringBuffer();
        retval.append("data/test/");
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1, colName.length());
        retval.append(colName);
        retval.append(".emptyCollection.version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    protected String getCanonicalFullCollectionName(Object object) {
        StringBuffer retval = new StringBuffer();
        retval.append("data/test/");
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1, colName.length());
        retval.append(colName);
        retval.append(".fullCollection.version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    /**
     * Write a Serializable or Externalizable object as
     * a file at the given path.  NOT USEFUL as part
     * of a unit test; this is just a utility method
     * for creating disk-based objects in CVS that can become
     * the basis for compatibility tests using
     * readExternalFormFromDisk(String path)
     *
     * @param o Object to serialize
     * @param path path to write the serialized Object
     */
    protected void writeExternalFormToDisk(Serializable o, String path) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(path);
        writeExternalFormToStream(o, fileStream);
    }

    /**
     * Converts a Serializable or Externalizable object to
     * bytes.  Useful for in-memory tests of serialization
     *
     * @param o Object to convert to bytes
     * @return serialized form of the Object
     */
    protected byte[] writeExternalFormToBytes(Serializable o) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        writeExternalFormToStream(o, byteStream);
        return byteStream.toByteArray();
    }

    /**
     * Reads a Serialized or Externalized Object from disk.
     * Useful for creating compatibility tests between
     * different CVS versions of the same class
     *
     * @param path path to the serialized Object
     * @return the Object at the given path
     */
    protected Object readExternalFormFromDisk(String path) throws IOException, ClassNotFoundException {
        FileInputStream stream = new FileInputStream(path);
        return readExternalFormFromStream(stream);
    }

    /**
     * Read a Serialized or Externalized Object from bytes.
     * Useful for verifying serialization in memory.
     *
     * @param b byte array containing a serialized Object
     * @return Object contained in the bytes
     */
    protected Object readExternalFormFromBytes(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        return readExternalFormFromStream(stream);
    }

    protected boolean skipSerializedCanonicalTests() {
        return Boolean.getBoolean("org.apache.commons.collections:with-clover");
    }

    // private implementation
    //-----------------------------------------------------------------------
    private Object readExternalFormFromStream(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream oStream = new ObjectInputStream(stream);
        return oStream.readObject();
    }

    private void writeExternalFormToStream(Serializable o, OutputStream stream) throws IOException {
        ObjectOutputStream oStream = new ObjectOutputStream(stream);
        oStream.writeObject(o);
    }

}
