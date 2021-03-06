/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrlaboratory.web.controller.queue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrlaboratory.LaboratoryService;
import org.openmrs.module.ehrlaboratory.web.util.LaboratoryUtil;
import org.openmrs.module.ehrlaboratory.web.util.TestModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("LaboratoryRescheduleTestController")
@RequestMapping("/module/ehrlaboratory/rescheduleTest.form")
public class RescheduleTestController {

	@ModelAttribute("order")
	public Order getOrder(@RequestParam("orderId") Integer orderId) {
		return Context.getOrderService().getOrder(orderId);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showRescheduleForm(Model model, @RequestParam(value="type", required=false) String type,
			@ModelAttribute("order") Order order) {
		if (order != null) {
			TestModel tm = LaboratoryUtil.generateModel(order, null);
			model.addAttribute("test", tm);
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			model.addAttribute("currentDate", LaboratoryUtil.formatDate(c.getTime()));
		}
		if(!StringUtils.isBlank(type)){
			if(type.equalsIgnoreCase("reorder")){
				return "/module/ehrlaboratory/worklist/reOrder";
			}				
		}			
		return "/module/ehrlaboratory/queue/rescheduleForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String rescheduleTest(Model model,
			@ModelAttribute("order") Order order, @RequestParam("rescheduledDate") String rescheduledDateStr) {
		if (order != null) {
			LaboratoryService ls = (LaboratoryService) Context.getService(LaboratoryService.class);
			Date rescheduledDate;
			try {
				rescheduledDate = LaboratoryUtil.parseDate(rescheduledDateStr);
				String status = ls.rescheduleTest(order, rescheduledDate);
				model.addAttribute("status", status);
			} catch (ParseException e) {
				e.printStackTrace();
				model.addAttribute("status", "Invalid date!");
			}
		}
		return "/module/ehrlaboratory/queue/rescheduleResponse";
	}

}
