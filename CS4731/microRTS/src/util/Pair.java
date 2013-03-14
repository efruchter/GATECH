package util;

/**
 * \package util
 * \brief Utilites. OLD code needed by Santi's AIs
 */

/**
 * \brief A pair of objects
 *
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1,T2> {
	public T1 m_a;
	public T2 m_b;
	
	public Pair(T1 a,T2 b) {
		m_a = a;
		m_b = b;
	}        
}
