/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.minecraftonline.griefalert.common.data.services;

import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandSource;

public interface DataService {

  /**
   * Queries all events matching the conditions in the {@link DataRequest} and restores the
   * original states within every event.
   *
   * @param source     The source requesting the rollback maneuver
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void rollback(@Nonnull CommandSource source, @Nonnull DataRequest conditions) throws Exception;

  /**
   * Queries all events matching the conditions in the {@link DataRequest} and restores the
   * final states within every event.
   *
   * @param source     The source requesting the restoration maneuver
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void restore(@Nonnull CommandSource source, @Nonnull DataRequest conditions) throws Exception;

  /**
   * Queries all events matching the conditions in the {@link DataRequest} and sends
   * the command source the matching information.
   *
   * @param source     The source requesting the information
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void lookup(@Nonnull CommandSource source, @Nonnull DataRequest conditions) throws Exception;

}
