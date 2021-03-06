package geogebra.common.export;

import geogebra.common.util.StringUtil;

public class MathmlTemplate {
	public static void mathml(StringBuilder sb, String op, String leftStr,
			String rightStr) {
		mathml(sb, op, "", leftStr, "", "", rightStr, "");
	}

	public static void mathml(StringBuilder sb, String op, String preL,
			String leftStr, String postL, String preR, String rightStr,
			String postR) {
		sb.append("<apply>");
		sb.append(op);
		sb.append(preL);

		if (leftStr.startsWith("<apply>")) {
			sb.append(leftStr);
		} else if (StringUtil.isNumber(leftStr)) {
			sb.append("<cn>");
			sb.append(leftStr);
			sb.append("</cn>");
		} else {
			sb.append("<ci>");
			sb.append(leftStr);
			sb.append("</ci>");
		}

		sb.append(postL);
		sb.append(preR);

		if (rightStr != null) {
			if (rightStr.startsWith("<apply>")) {
				sb.append(rightStr);
			} else if (StringUtil.isNumber(rightStr)) {
				sb.append("<cn>");
				sb.append(rightStr);
				sb.append("</cn>");
			} else {
				sb.append("<ci>");
				sb.append(rightStr);
				sb.append("</ci>");
			}
		}

		sb.append(postR);

		sb.append("</apply>");

	}
}
